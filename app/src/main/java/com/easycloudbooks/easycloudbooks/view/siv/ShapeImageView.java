package com.easycloudbooks.easycloudbooks.view.siv;

import android.content.Context;
import android.util.AttributeSet;

import com.easycloudbooks.easycloudbooks.view.siv.shader.ShaderHelper;
import com.easycloudbooks.easycloudbooks.view.siv.shader.SvgShader;

public class ShapeImageView extends ShaderImageView {

    public ShapeImageView(Context context) {
        super(context);
    }

    public ShapeImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ShapeImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public ShaderHelper createImageViewHelper() {
        return new SvgShader();
    }
}
