package com.github.nomisrev.androidarrowplayground.ui.main

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.github.nomisrev.androidarrowplayground.R
import com.github.nomisrev.androidarrowplayground.onClick
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val instances = instances()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        validation.onClick {
            instances.navigator().goToValidation()
        }

        tictactoe.onClick {
            instances.navigator().goToTicTacToe()
        }

    }
    
}
