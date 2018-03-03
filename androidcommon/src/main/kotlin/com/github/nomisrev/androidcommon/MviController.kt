package com.github.nomisrev.androidcommon

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bluelinelabs.conductor.Controller
import com.github.nomisrev.androidarrowplayground.domain.MviView
import com.jakewharton.rxrelay2.BehaviorRelay
import com.jakewharton.rxrelay2.Relay
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

/**
 * [MviController] renders a model [A].
 *
 * Since Arrow does not provide `Stream<F, ?>` yet we choose Rx here as a medium to communicate to our domain logic.
 *
 * Idea is to expose `UiEvent` to the domain logic in the form of a stream (Observable).
 * Remember to always decouple them to not create any memory leaks, you can do this by using RxRelay or a Subject.
 *
 * The domain logic can communicate back to us by posting the model to `state`.
 * [MviController] calls `render` on every event on `state`.
 */
abstract class MviController<A : Any>(args: Bundle = Bundle()) : Controller(args), MviView<A> {

    override val state: Relay<A> = BehaviorRelay.create<A>().toSerialized()

    private val reactiveControllerDelegate = ReactiveControllerDelegate()
    protected val rxLifecycle: ReactiveController get() = reactiveControllerDelegate

    /**
     * Inflate your view here by calling inflater.inflate(layoutResId, container, false)
     *
     * All layout init should be done here as well. You can do this by chaining `.also { }`
     */
    protected abstract fun inflateView(inflater: LayoutInflater, container: ViewGroup): View

    /**
     * Render method.
     *
     * @param view layout of the Controller.
     * @param model to render.
     */
    protected abstract fun render(view: View, model: A): Unit

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        reactiveControllerDelegate.createView()
        return inflateView(inflater, container)
    }

    override fun onAttach(view: View) {
        super.onAttach(view)
        reactiveControllerDelegate.lifecycleRelay
                .filter { it == ControllerLifecycle.Attach }
                .switchMap { state }
                .observeOn(AndroidSchedulers.mainThread())
                .takeUntil(reactiveControllerDelegate.untilDetach())
                .subscribe { render(view, it) }
        reactiveControllerDelegate.attach()
    }

    override fun onDetach(view: View) {
        super.onDetach(view)
        reactiveControllerDelegate.detach()
    }

    override fun onDestroyView(view: View) {
        super.onDestroyView(view)
        reactiveControllerDelegate.destroyView()
    }

    public override fun onDestroy() {
        super.onDestroy()
        reactiveControllerDelegate.destroy()
    }

    fun Disposable.disposeOnDetach(): Unit = this.let { disposable ->
        rxLifecycle.untilDetach()
                .firstOrError()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ _, _ -> disposable.dispose() })
    }

    sealed class ControllerLifecycle {
        object CreateView : ControllerLifecycle()
        object Attach : ControllerLifecycle()
        object Detach : ControllerLifecycle()
        object DestroyView : ControllerLifecycle()
        object Destroy : ControllerLifecycle()
    }

    interface ReactiveController {
        fun untilCreateView(): Observable<ControllerLifecycle>
        fun untilAttach(): Observable<ControllerLifecycle>
        fun untilDetach(): Observable<ControllerLifecycle>
        fun untilDestroyView(): Observable<ControllerLifecycle>
        fun untilDestroy(): Observable<ControllerLifecycle>
    }

    /**
     * Delegate class for Android lifecycle in a Controller screen to transform them on reactive streams.
     * It wraps the lifecycle in a more comprehensible approach for this app.
     */
    interface ReactiveControllerDelegate : ReactiveController {

        companion object {
            operator fun invoke() = object : ReactiveControllerDelegate {
                override val lifecycleRelay: Relay<ControllerLifecycle> = BehaviorRelay.create()
            }
        }

        val lifecycleRelay: Relay<ControllerLifecycle>

        fun createView() = lifecycleRelay.accept(ControllerLifecycle.CreateView)
        fun attach() = lifecycleRelay.accept(ControllerLifecycle.Attach)
        fun detach() = lifecycleRelay.accept(ControllerLifecycle.Detach)
        fun destroyView() = lifecycleRelay.accept(ControllerLifecycle.DestroyView)
        fun destroy() = lifecycleRelay.accept(ControllerLifecycle.Destroy)

        override fun untilCreateView(): io.reactivex.Observable<ControllerLifecycle> =
                lifecycleRelay.filter { it == ControllerLifecycle.CreateView }

        override fun untilAttach(): io.reactivex.Observable<ControllerLifecycle> =
                lifecycleRelay.filter { it == ControllerLifecycle.Attach }

        override fun untilDetach(): io.reactivex.Observable<ControllerLifecycle> =
                lifecycleRelay.filter { it == ControllerLifecycle.Detach }

        override fun untilDestroyView(): io.reactivex.Observable<ControllerLifecycle> =
                lifecycleRelay.filter { it == ControllerLifecycle.DestroyView }

        override fun untilDestroy(): io.reactivex.Observable<ControllerLifecycle> =
                lifecycleRelay.filter { it == ControllerLifecycle.Destroy }

    }

}
