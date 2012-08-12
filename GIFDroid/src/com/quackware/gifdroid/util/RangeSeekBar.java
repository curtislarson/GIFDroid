package com.quackware.gifdroid.util;

import java.math.BigDecimal;

import com.quackware.gifdroid.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

/**
 * Widget that lets users select a minimum and maximum value on a given numerical range.
 * The range value types can be one of Long, Double, Integer, Float, Short, Byte or BigDecimal.
 * 
 * @author Stephan Tittel (stephan.tittel@kom.tu-darmstadt.de)
 * @author Peter Sinnott (psinnott@gmail.com)
 *
 * @param <T> The Number type of the range values. One of Long, Double, Integer, Float, Short, Byte or BigDecimal.
 */
public class RangeSeekBar<T extends Number> extends ImageView {
        private final Paint paint = new Paint();
        private final Bitmap thumbImage = BitmapFactory.decodeResource(getResources(), R.drawable.seek_thumb_normal);
        private final Bitmap thumbPressedImage = BitmapFactory.decodeResource(getResources(), R.drawable.seek_thumb_pressed);
        private final float thumbWidth = thumbImage.getWidth();
        private final float thumbHalfWidth = 0.5f * thumbWidth;
        private final float thumbHalfHeight = 0.5f * thumbImage.getHeight();
        private final float lineHeight = 0.3f * thumbHalfHeight;
        private final float padding = thumbHalfWidth;
        private T absoluteMinValue, absoluteMaxValue;
        private NumberType numberType;
        private double absoluteMinValuePrim, absoluteMaxValuePrim;
        private double normalizedMinValue = 0d;
        private double normalizedMaxValue = 1d;
        private Thumb pressedThumb = null;
        private boolean notifyWhileDragging = false;
        private OnRangeSeekBarChangeListener<T> listener;

        /**
         * Creates a new RangeSeekBar.
         * @param absoluteMinValue The minimum value of the selectable range.
         * @param absoluteMaxValue The maximum value of the selectable range.
         * @param context
         * @throws IllegalArgumentException Will be thrown if min/max value type is not one of Long, Double, Integer, Float, Short, Byte or BigDecimal.
         */
        public RangeSeekBar(T absoluteMinValue, T absoluteMaxValue, Context context) throws IllegalArgumentException {
                super(context);
                this.absoluteMinValue = absoluteMinValue;
                this.absoluteMaxValue = absoluteMaxValue;
                absoluteMinValuePrim = absoluteMinValue.doubleValue();
                absoluteMaxValuePrim = absoluteMaxValue.doubleValue();
                numberType = NumberType.fromNumber(absoluteMinValue);
        }
        
        public RangeSeekBar(Context context, AttributeSet attrs)
        {
        	super(context,attrs);
        	numberType = NumberType.INTEGER;
        	absoluteMinValuePrim = 0;
        	absoluteMaxValuePrim = 0;
        	absoluteMinValue = null;
        	absoluteMaxValue = null;
        	
        }
        
        public void setValues(T absoluteMinValue, T absoluteMaxValue)
        {
        	this.absoluteMinValue = absoluteMinValue;
            this.absoluteMaxValue = absoluteMaxValue;
            absoluteMinValuePrim = absoluteMinValue.doubleValue();
            absoluteMaxValuePrim = absoluteMaxValue.doubleValue();
            numberType = NumberType.fromNumber(absoluteMinValue);
        }

        public boolean isNotifyWhileDragging() {
                return notifyWhileDragging;
        }

        /**
         * Should the widget notify the listener callback while the user is still dragging a thumb? Default is false.
         * @param flag
         */
        public void setNotifyWhileDragging(boolean flag) {
                this.notifyWhileDragging = flag;
        }

        /**
         * Returns the absolute minimum value of the range that has been set at construction time.
         * @return The absolute minimum value of the range.
         */
        public T getAbsoluteMinValue() {
                return absoluteMinValue;
        }

        /**
         * Returns the absolute maximum value of the range that has been set at construction time.
         * @return The absolute maximum value of the range.
         */
        public T getAbsoluteMaxValue() {
                return absoluteMaxValue;
        }

        /**
         * Returns the currently selected min value.
         * @return The currently selected min value.
         */
        public T getSelectedMinValue() {
                return normalizedToValue(normalizedMinValue);
        }

        /**
         * Sets the currently selected minimum value. The widget will be invalidated and redrawn.
         * @param value The Number value to set the minimum value to. Will be clamped to given absolute minimum/maximum range.
         */
        public void setSelectedMinValue(T value) {
                // in case absoluteMinValue == absoluteMaxValue, avoid division by zero when normalizing.
                if (0 == (absoluteMaxValuePrim - absoluteMinValuePrim)) {
                        setNormalizedMinValue(0d);
                }
                else {
                        setNormalizedMinValue(valueToNormailzed(value));
                }
        }

        /**
         * Returns the currently selected max value.
         * @return The currently selected max value.
         */
        public T getSelectedMaxValue() {
                return normalizedToValue(normalizedMaxValue);
        }

        /**
         * Sets the currently selected maximum value. The widget will be invalidated and redrawn.
         * @param value The Number value to set the maximum value to. Will be clamped to given absolute minimum/maximum range.
         */
        public void setSelectedMaxValue(T value) {
                // in case absoluteMinValue == absoluteMaxValue, avoid division by zero when normalizing.
                if (0 == (absoluteMaxValuePrim - absoluteMinValuePrim)) {
                        setNormalizedMaxValue(1d);
                }
                else {
                        setNormalizedMaxValue(valueToNormailzed(value));
                }
        }

        /**
         * Registers given listener callback to notify about changed selected values.
         * @param listener The listener to notify about changed selected values.
         */
        public void setOnRangeSeekBarChangeListener(OnRangeSeekBarChangeListener<T> listener) {
                this.listener = listener;
        }

        /**
         * Handles thumb selection and movement. Notifies listener callback on certain events.
         */
        @Override
        public boolean onTouchEvent(MotionEvent event) {
                switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                        pressedThumb = evalPressedThumb(event.getX());
                        invalidate();
                        break;
                case MotionEvent.ACTION_MOVE:
                        if (pressedThumb != null) {
                                if (Thumb.MIN.equals(pressedThumb)) {
                                        setNormalizedMinValue(screenToNormalized(event.getX()));
                                }
                                else if (Thumb.MAX.equals(pressedThumb)) {
                                        setNormalizedMaxValue(screenToNormalized(event.getX()));
                                }
                                if (notifyWhileDragging && listener != null) {
                                        listener.rangeSeekBarValuesChanged(getSelectedMinValue(), getSelectedMaxValue());
                                }
                        }
                        break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                        pressedThumb = null;
                        invalidate();
                        if (listener != null) {
                                listener.rangeSeekBarValuesChanged(getSelectedMinValue(), getSelectedMaxValue());
                        }
                        break;
                }
                return true;
        }

        /**
         * Ensures correct size of the widget.
         */
        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                int width = 200;
                if (MeasureSpec.UNSPECIFIED != MeasureSpec.getMode(widthMeasureSpec)) {
                        width = MeasureSpec.getSize(widthMeasureSpec);
                }
                int height = thumbImage.getHeight();
                if (MeasureSpec.UNSPECIFIED != MeasureSpec.getMode(heightMeasureSpec)) {
                        height = Math.min(height, MeasureSpec.getSize(heightMeasureSpec));
                }
                setMeasuredDimension(width, height);
        }

        /**
         * Draws the widget on the given canvas.
         */
        @Override
        protected void onDraw(Canvas canvas) {
                super.onDraw(canvas);
                // draw seek bar background line
                RectF rect = new RectF(padding, 0.5f * (getHeight() - lineHeight), getWidth() - padding, 0.5f * (getHeight() + lineHeight));
                paint.setStyle(Style.FILL);
                paint.setColor(Color.GRAY);
                canvas.drawRect(rect, paint);
                // draw seek bar active range line
                rect.left = normalizedToScreen(normalizedMinValue);
                rect.right = normalizedToScreen(normalizedMaxValue);
                // orange color
                paint.setColor(Color.rgb(255, 165, 0));
                canvas.drawRect(rect, paint);
                // draw minimum thumb
                drawThumb(normalizedToScreen(normalizedMinValue), Thumb.MIN.equals(pressedThumb), canvas);
                // draw maximum thumb
                drawThumb(normalizedToScreen(normalizedMaxValue), Thumb.MAX.equals(pressedThumb), canvas);
        }

        /**
         * Overridden to save instance state when device orientation changes. This method is called automatically if you assign an id to the RangeSeekBar
         * widget using the {@link #setId(int)} method. Other members of this class than the normalized min and max values don't need to be saved.
         */
        @Override
        protected Parcelable onSaveInstanceState() {
                Bundle bundle = new Bundle();
                bundle.putParcelable("SUPER", super.onSaveInstanceState());
                bundle.putDouble("MIN", normalizedMinValue);
                bundle.putDouble("MAX", normalizedMaxValue);
                return bundle;
        }

        /**
         * Overridden to restore instance state when device orientation changes. This method is called automatically if you assign an id to the RangeSeekBar
         * widget using the {@link #setId(int)} method. 
         */
        @Override
        protected void onRestoreInstanceState(Parcelable parcel) {
                Bundle bundle = (Bundle) parcel;
                super.onRestoreInstanceState(bundle.getParcelable("SUPER"));
                normalizedMinValue = bundle.getDouble("MIN");
                normalizedMaxValue = bundle.getDouble("MAX");
        }

        /**
         * Draws the "normal" resp. "pressed" thumb image on specified x-coordinate.
         * @param screenCoord The x-coordinate in screen space where to draw the image.
         * @param pressed Is the thumb currently in "pressed" state?
         * @param canvas The canvas to draw upon.
         */
        private void drawThumb(float screenCoord, boolean pressed, Canvas canvas) {
                canvas.drawBitmap(pressed ? thumbPressedImage : thumbImage, screenCoord - thumbHalfWidth, ((0.5f * getHeight()) - thumbHalfHeight), paint);
        }

        /**
         * Decides which (if any) thumb is touched by the given x-coordinate.
         * @param touchX The x-coordinate of a touch event in screen space.
         * @return The pressed thumb or null if none has been touched.
         */
        private Thumb evalPressedThumb(float touchX) {
                Thumb result = null;
                boolean minThumbPressed = isInThumbRange(touchX, normalizedMinValue);
                boolean maxThumbPressed = isInThumbRange(touchX, normalizedMaxValue);
                if (minThumbPressed && maxThumbPressed) {
                        // if both thumbs are pressed (they lie on top of each other), choose the one with more room to drag. this avoids "stalling" the thumbs in a corner, not being able to drag them apart anymore.
                        result = (touchX / getWidth() > 0.5f) ? Thumb.MIN : Thumb.MAX;
                }
                else if (minThumbPressed) {
                        result = Thumb.MIN;
                }
                else if (maxThumbPressed) {
                        result = Thumb.MAX;
                }
                return result;
        }

        /**
         * Decides if given x-coordinate in screen space needs to be interpreted as "within" the normalized thumb x-coordinate.
         * @param touchX The x-coordinate in screen space to check.
         * @param normalizedThumbValue The normalized x-coordinate of the thumb to check. 
         * @return true if x-coordinate is in thumb range, false otherwise.
         */
        private boolean isInThumbRange(float touchX, double normalizedThumbValue) {
                return Math.abs(touchX - normalizedToScreen(normalizedThumbValue)) <= thumbHalfWidth;
        }

        /** 
         * Sets normalized min value to value so that 0 <= value <= normalized max value <= 1.
         * The View will get invalidated when calling this method. 
         * @param value The new normalized min value to set.
         */
        private void setNormalizedMinValue(double value) {
                normalizedMinValue = Math.max(0d, Math.min(1d, Math.min(value, normalizedMaxValue)));
                invalidate();
        }

        /** 
         * Sets normalized max value to value so that 0 <= normalized min value <= value <= 1.
         * The View will get invalidated when calling this method. 
         * @param value The new normalized max value to set.
         */
        private void setNormalizedMaxValue(double value) {
                normalizedMaxValue = Math.max(0d, Math.min(1d, Math.max(value, normalizedMinValue)));
                invalidate();
        }
        
        /**
         * Converts a normalized value to a Number object in the value space between absolute minimum and maximum.
         * @param normalized
         * @return
         */
        @SuppressWarnings("unchecked")
        private T normalizedToValue(double normalized) {
                return (T) numberType.toNumber(absoluteMinValuePrim + normalized * (absoluteMaxValuePrim - absoluteMinValuePrim));
        }
        
        /**
         * Converts the given Number value to a normalized double.
         * @param value The Number value to normalize.
         * @return The normalized double.
         */
        private double valueToNormailzed(T value) {
                if (0 == absoluteMaxValuePrim - absoluteMinValuePrim) {
                        // prevent division by zero, simply return 0.
                        return 0d;
                }
                return (value.doubleValue() - absoluteMinValuePrim) / (absoluteMaxValuePrim - absoluteMinValuePrim);
        }

        /**
         * Converts a normalized value into screen space.
         * @param normalizedCoord The normalized value to convert.
         * @return The converted value in screen space.
         */
        private float normalizedToScreen(double normalizedCoord) {
                return (float) (padding + normalizedCoord * (getWidth() - 2 * padding));
        }

        /**
         * Converts screen space x-coordinates into normalized values.
         * @param screenCoord The x-coordinate in screen space to convert.
         * @return The normalized value.
         */
        private double screenToNormalized(float screenCoord) {
                int width = getWidth();
                if (width <= 2 * padding) {
                        // prevent division by zero, simply return 0.
                        return 0d;
                }
                else {
                        double result = (screenCoord - padding) / (width - 2 * padding);
                        return Math.min(1d, Math.max(0d, result));
                }
        }

        /**
         * Callback listener interface to notify about changed range values.
         * @author Stephan Tittel (stephan.tittel@kom.tu-darmstadt.de)
         *
         * @param <T> The Number type the RangeSeekBar has been declared with.
         */
        public interface OnRangeSeekBarChangeListener<T> {
                void rangeSeekBarValuesChanged(T minValue, T maxValue);
        }
        
        /**
         * Thumb constants (min and max).
         * @author Stephan Tittel (stephan.tittel@kom.tu-darmstadt.de)
         *
         */
        private static enum Thumb {
                MIN, MAX
        };

        /**
         * Utility enumaration used to convert between Numbers and doubles.
         * @author Stephan Tittel (stephan.tittel@kom.tu-darmstadt.de)
         *
         */
        private static enum NumberType {
                LONG, DOUBLE, INTEGER, FLOAT, SHORT, BYTE, BIG_DECIMAL;

                public static <E extends Number> NumberType fromNumber(E value) throws IllegalArgumentException {
                        if (value instanceof Long) {
                                return LONG;
                        }
                        if (value instanceof Double) {
                                return DOUBLE;
                        }
                        if (value instanceof Integer) {
                                return INTEGER;
                        }
                        if (value instanceof Float) {
                                return FLOAT;
                        }
                        if (value instanceof Short) {
                                return SHORT;
                        }
                        if (value instanceof Byte) {
                                return BYTE;
                        }
                        if (value instanceof BigDecimal) {
                                return BIG_DECIMAL;
                        }
                        throw new IllegalArgumentException("Number class '" + value.getClass().getName() + "' is not supported");
                }
                
                public Number toNumber(double value) {
                        switch (this) {
                        case LONG:
                                return new Long((long) value);
                        case DOUBLE:
                                return new Double(value);
                        case INTEGER:
                                return new Integer((int)value);
                        case FLOAT:
                                return new Float(value);
                        case SHORT:
                                return new Short((short)value);
                        case BYTE:
                                return new Byte((byte)value);
                        case BIG_DECIMAL:
                                return new BigDecimal(value);
                        }
                        throw new InstantiationError("can't convert " + this + " to a Number object");
                }
        }
}