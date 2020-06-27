package com.example.parthpatel.face_tracker;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.Landmark;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

/**
 * Graphic instance for rendering face position, orientation, and landmarks within an associated
 * graphic overlay view.
 */
class FaceGraphic extends GraphicOverlay.Graphic {
    private static final float FACE_POSITION_RADIUS = 10.0f;
    private static final float ID_TEXT_SIZE = 40.0f;
    private static final float ID_Y_OFFSET = 50.0f;
    private static final float ID_X_OFFSET = -50.0f;
    private static final float BOX_STROKE_WIDTH = 5.0f;

    private static final int COLOR_CHOICES[] = {
            Color.BLUE,
            Color.CYAN,
            Color.GREEN,
            Color.MAGENTA,
            Color.RED,
            Color.WHITE,
            Color.YELLOW
    };
    private static int mCurrentColorIndex = 0;

    private Paint mFacePositionPaint;
    private Paint mIdPaint;
    private Paint mBoxPaint;

    private volatile Face mFace;
    private int mFaceId;
    private float mFaceHappiness;

    FaceGraphic(GraphicOverlay overlay) {
        super(overlay);

        mCurrentColorIndex = (mCurrentColorIndex + 1) % COLOR_CHOICES.length;
        final int selectedColor = COLOR_CHOICES[mCurrentColorIndex];

        mFacePositionPaint = new Paint();
        mFacePositionPaint.setColor(selectedColor);

        mIdPaint = new Paint();
        mIdPaint.setColor(selectedColor);
        mIdPaint.setTextSize(ID_TEXT_SIZE);

        mBoxPaint = new Paint();
        mBoxPaint.setColor(selectedColor);
        mBoxPaint.setStyle(Paint.Style.STROKE);
        mBoxPaint.setStrokeWidth(BOX_STROKE_WIDTH);
    }

    void setId(int id) {
        mFaceId = id;
    }


    /**
     * Updates the face instance from the detection of the most recent frame.  Invalidates the
     * relevant portions of the overlay to trigger a redraw.
     */
    void updateFace(Face face) {
        mFace = face;
        postInvalidate();
    }

    /**
     * Draws the face annotations for position on the supplied canvas.
     */
    @Override
    public void draw(Canvas canvas) {
        Face face = mFace;
        if (face == null) {
            return;
        }

        //custom work for database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("position");



        float a = translateX(face.getPosition().x);
        float b = translateY(face.getPosition().y);

        List<Landmark> landmarkList =  face.getLandmarks();

            int temp = landmarkList.size();
      //  canvas.drawText(""+temp,300,200,mIdPaint);
           // PointF t = temp.getPosition();
            //float p = t.x;
            //float q = t.y;
           //canvas.drawCircle(p,q,FACE_POSITION_RADIUS,mFacePositionPaint);

        // Draws a circle at the position of the detected face, with the face's track id below.
        float x = translateX(face.getPosition().x + face.getWidth() / 2);
        float y = translateY(face.getPosition().y + face.getHeight() / 2);
        //canvas.drawCircle(x, y, FACE_POSITION_RADIUS, mFacePositionPaint);
        //canvas.drawLine(0,0, a, b,mIdPaint);
        //canvas.drawText("right "+a+" "+b,a,b,mIdPaint);

        float xOffset = scaleX(face.getWidth() / 2.0f);
        float yOffset = scaleY(face.getHeight() / 2.0f);
        float left = x - xOffset;
        float top = y - yOffset;
        float right = x + xOffset;
        float bottom = y + yOffset;

        //custom work for database
        myRef.setValue("x axis => "+x+"y axis => "+y);


        //canvas.drawText("id: " + mFaceId, x + ID_X_OFFSET, y + ID_Y_OFFSET, mIdPaint);
        //canvas.drawText("happiness: " + String.format("%.2f", face.getIsSmilingProbability()), left + (face.getWidth()/2), bottom - (face.getHeight()/2), mIdPaint);
        //canvas.drawText("right eye: " + "\n"+String.format("%.2f", face.getIsRightEyeOpenProbability()), a - (face.getWidth()/2), b + (face.getHeight()), mIdPaint);
        //canvas.drawText("left eye: " + "\n"+String.format("%.2f", face.getIsLeftEyeOpenProbability()), left + (face.getWidth()/2), b + (face.getHeight()), mIdPaint);



            canvas.drawText("happiness: " + String.format("%.2f", face.getIsSmilingProbability()), left, bottom + 40.f, mIdPaint);
            canvas.drawText("left eye: " + "\n" + String.format("%.2f", face.getIsRightEyeOpenProbability()), left, bottom + 90.f, mIdPaint);
            canvas.drawText("right eye: " + "\n" + String.format("%.2f", face.getIsLeftEyeOpenProbability()), left, bottom + 140.f, mIdPaint);

        // Draws a bounding box around the face.

        //canvas.drawLine(1080,0, left, top,mIdPaint);
        //canvas.drawText("L "+left+" "+top,left,top,mIdPaint);

        canvas.drawRect(left, top, right, bottom, mBoxPaint);
    }
}
