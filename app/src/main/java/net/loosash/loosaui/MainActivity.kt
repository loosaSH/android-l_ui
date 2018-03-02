package net.loosash.loosaui

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

//    private var tvTest: TextView? = null

    private var mContext: Context? = null

    private val main: LinearLayout? = null
    private var rlTest: RelativeLayout? = null
    private var tvTest: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//                final PathView path_view = (PathView) findViewById(R.id.path);
//                path_view.init();
        mContext = this
        iv_test.setOnClickListener{
//            val mShowAction = TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
//                    Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
//                    -1.0f, Animation.RELATIVE_TO_SELF, 0.0f)
//            mShowAction.setDuration(1000)
//            tv_marquee.startAnimation(mShowAction);
            if( broadcastview.visibility == View.VISIBLE){
                broadcastview.visibility = View.GONE
            }else{
                broadcastview.visibility = View.VISIBLE
            }

        }

    }



}
