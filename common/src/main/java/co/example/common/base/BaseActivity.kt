package co.example.common.base

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewbinding.ViewBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

abstract class BaseActivity<D:ViewBinding> : AppCompatActivity(){

    protected lateinit var binding:D
    protected var activityContext: Activity? = null
    protected abstract fun viewBindingLayout(): D
    protected abstract fun initializeView(savedInstanceState: Bundle?)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = viewBindingLayout()
        setContentView(binding.root)
        activityContext = this
        initializeView(savedInstanceState)
    }


    protected fun showMessage(message:String?){
        Snackbar.make(findViewById(android.R.id.content),""+message, Snackbar.LENGTH_LONG).show()
    }

    protected fun showToastMessage(message:String?){
        Toast.makeText(applicationContext,message, Toast.LENGTH_SHORT).show()
    }

    protected fun showProgressBar(isLoading: Boolean,view: View){
        if (isLoading) {
            view.visibility = View.VISIBLE
            window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        } else {
            view.visibility = View.GONE
            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) finish()
        return super.onOptionsItemSelected(item)
    }

    protected inline fun<T> Flow<T>.execute(crossinline action:(T)->Unit){
        with(this){
            lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.STARTED){
                    collect{
                        action(it)
                    }
                }
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        activityContext = null
        //finish()
    }

    @SuppressLint("UnsafeRepeatOnLifecycleDetector")
    protected inline infix fun <T> Flow<T>.bindTo(crossinline action: (T) -> Unit) {
        with(this) {
            lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.STARTED){
                    collect{
                        action(it)
                    }
                }
            }
        }
    }
}