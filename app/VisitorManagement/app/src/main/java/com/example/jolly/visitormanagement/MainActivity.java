package com.example.jolly.visitormanagement;

import android.animation.Animator;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {


    ImageView imageView;
    Button cont;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cont = findViewById(R.id.cont);
        imageView = (ImageView) findViewById(R.id.imageView);

        imageView.setBackgroundResource(R.drawable.anim);
        AnimationDrawable anim = (AnimationDrawable) imageView.getBackground();
        anim.start();

        fade();

        cont.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                update();
            }
        });

    }
    private void update() {
        Intent i = new Intent(this,Home.class);
        startActivity(i);
        finish();
    }

    public void fade() {
        ImageView logo = (ImageView)findViewById(R.id.imageView);
        ViewPropertyAnimator anim = logo.animate();

        anim.setListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                Intent i = new Intent(MainActivity.this,Home.class);
                startActivity(i);
                finish();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                // TODO Auto-generated method stub

            }
        });
        anim.alpha(0f).setDuration(1700).start();
    }

}
