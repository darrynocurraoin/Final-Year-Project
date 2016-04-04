package org.opencv.samples.facedetect;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.imgproc.Imgproc;

import android.widget.Toast; 
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Button;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.Cursor;


public class FdActivity extends Activity implements CvCameraViewListener2 {

    private static final String    TAG                 = "Project::MainActivity";
    private static final String    DCDEBUG             = "darrynDebug";
    private static final Scalar    FACE_RECT_COLOR     = new Scalar(255, 0, 0, 255);	// Red
    private static final Scalar    LINE_COLOR     	   = new Scalar(0, 0, 255, 255);	// Green

    private TextView numberOfRepsText;
    private TextView lastDbRepEntry;
    private Button repsToDB;
    private SQLiteDatabase db;

    private Mat                    mRgba;
    private Mat                    mGray;
    private File                   mCascadeFile;
    private DetectionBasedTracker  mNativeDetector;
    private float                  mRelativeFaceSize   = 0.2f;
    private int                    mAbsoluteFaceSize   = 0;
    private CameraBridgeViewBase   mOpenCvCameraView;

    private Point				   p1;
    private Point				   p2;
    private int screenHeight;
    private int screenWidth;
    
    final Handler myHandler = new Handler();
    private int repCount = 0;
    int repTestFlag = 0;
    
    String id;
    String repDisplay;

    final Runnable updateRepCountResult = new Runnable() {
    	public void run() {
    		updateRepCount();
    	}
    };
    
    final Runnable updateLastRepQuery = new Runnable() {
    	public void run() {
    		updateLastSet();
    	}
    };

    private BaseLoaderCallback  mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");
                    // Load native library after(!) OpenCV initialization
                    System.loadLibrary("detection_based_tracker");

                    try {
                        // load cascade file from application resources
                        InputStream is = getResources().openRawResource(R.raw.lbpcascade_weightplate2);
                        File cascadeDir = getDir("cascade", Context.MODE_PRIVATE);
                        mCascadeFile = new File(cascadeDir, "lbpcascade_weightplate2.xml");
                        FileOutputStream os = new FileOutputStream(mCascadeFile);

                        byte[] buffer = new byte[4096];
                        int bytesRead;
                        while ((bytesRead = is.read(buffer)) != -1) {
                            os.write(buffer, 0, bytesRead);
                        }
                        is.close();
                        os.close();

                        mNativeDetector = new DetectionBasedTracker(mCascadeFile.getAbsolutePath(), 0);

                        cascadeDir.delete();

                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.e(TAG, "Failed to load cascade. Exception thrown: " + e);
                    }

                    mOpenCvCameraView.enableView();
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    public FdActivity() {
    	Log.i(TAG, "Instantiated new " + this.getClass());
    }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "called onCreate");
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.rep_count_view);

        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.cameraView);
        mOpenCvCameraView.setMaxFrameSize(1280, 720);
        mOpenCvCameraView.setCvCameraViewListener(this);
        mOpenCvCameraView.enableFpsMeter();
        
        numberOfRepsText =(TextView) findViewById(R.id.numberOfReps);
        lastDbRepEntry =(TextView) findViewById(R.id.lastDbRepEntry);
        repsToDB = (Button) findViewById(R.id.repsToDB);
        
        repsToDB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    insertDbData();
                    readDbData();
                    repCount = 0;

                } catch (Exception e) {
                    Log.i(DCDEBUG, "ERROR WITH ONLICK LISTENER: " + e.getMessage());
                }
            }
        });    

        getScreenHeightWidth();
        openDatabase();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    public void openDatabase() {
        try {
            String SDcardPath = "data/data/org.opencv.samples.facedetect";
            String DBpath = SDcardPath + "/" + "projectDB.db";
            Log.i(DCDEBUG, "DB Path: " + DBpath);
            db = SQLiteDatabase.openDatabase(DBpath, null, SQLiteDatabase.CREATE_IF_NECESSARY);
            Log.i(DCDEBUG, "DB Opened ");
        } catch (SQLiteException e) {
            Log.i(DCDEBUG, "Error opening DB: " + e.getMessage());
            finish();
        }
        
    }

    public void insertDbData() {
        // create the table
        db.beginTransaction();
        try {
            // need to edit XML
            db.execSQL("create table repTable("
                + "tblID integer PRIMARY KEY autoincrement, "
                + "Reps integer); ");
            db.setTransactionSuccessful();
            Log.i(DCDEBUG, "Table created successfully");
        } catch (SQLException e1) {
            Log.i(DCDEBUG, "Error creating table: " + e1.getMessage());
            finish();
        }

        finally {
            db.endTransaction();
        }

        // populate the table
        db.beginTransaction();
        try {
            // need to edit XML
            db.execSQL("insert into repTable(Reps) values ('"+repCount+"');");
            db.setTransactionSuccessful();
            Log.i(DCDEBUG, repCount + " inserted into table successfully");
        } catch (SQLException e2) {
            Log.i(DCDEBUG, "Error inserting into DB: " + e2.getMessage());
            finish();
        }

        finally {
            db.endTransaction();
        }
    }

    public void readDbData() {
    	int i1 = 0;
    	int i2 = 1;
        db.beginTransaction();

        try {
            Cursor curs =  db.rawQuery("SELECT * FROM repTable where tblID = (select max(tblID) from repTable)", null);
            curs.moveToNext();

            id = curs.getString(i1);
            repDisplay = curs.getString(i2);

            myHandler.post(updateLastRepQuery);
            i1+=2; i2+=2;
            db.setTransactionSuccessful();
        } catch (SQLException e) {
            Log.i(DCDEBUG, "Error reading from DB: " + e.getMessage());
        }
        finally {
            db.endTransaction();
        }
    }

    public void dropTable() {
        // (clean start) action query to drop table
    	db.beginTransaction();
        try {
            db.execSQL("DROP TABLE IF EXISTS repTable;");
            db.setTransactionSuccessful();
            Log.i(DCDEBUG, "Table dropped successfully");
        } catch (Exception e) {
            Log.i(DCDEBUG, "Table dropped error: " + e.getMessage());
            finish(); 
        }
        finally {
            db.endTransaction();
        }
    }

    public void getScreenHeightWidth() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        screenHeight = metrics.heightPixels;
        screenWidth = metrics.widthPixels;
        p1 = new Point(0,screenHeight-600);
        p2 = new Point(screenWidth,screenHeight-600);
    }

    public void onDestroy() {
        super.onDestroy();
        mOpenCvCameraView.disableView();
        dropTable();
        db.close();
    }

    public void onCameraViewStarted(int width, int height) {
        mGray = new Mat();
        mRgba = new Mat();
    }

    public void onCameraViewStopped() {
        mGray.release();
        mRgba.release();
        repCount = 0;
    }

    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
        mRgba = inputFrame.rgba();
        mGray = inputFrame.gray();

        if (mAbsoluteFaceSize == 0) {
            int height = mGray.rows();
            if (Math.round(height * mRelativeFaceSize) > 0) {
                mAbsoluteFaceSize = Math.round(height * mRelativeFaceSize);
            }
            mNativeDetector.setMinFaceSize(mAbsoluteFaceSize);
        }

        MatOfRect faces = new MatOfRect();

        if (mNativeDetector != null)
                mNativeDetector.detect(mGray, faces);
        else 
            Log.e(TAG, "Detection method is not selected!");
        
    	Imgproc.line(mRgba, p1, p2, LINE_COLOR,8);
        
        Rect[] facesArray = faces.toArray();
        for (int i = 0; i < facesArray.length; i++){
            Imgproc.rectangle(mRgba, facesArray[i].tl(), facesArray[i].br(), FACE_RECT_COLOR, 3); 
            if(facesArray[i].y < p1.y && repTestFlag == 0){
            	repCount++;
            	repTestFlag = 1;
            }
            if(facesArray[i].y > p1.y)
            	repTestFlag = 0;
        }
        
        myHandler.post(updateRepCountResult);
        return mRgba;
    }
    
    public void updateRepCount() {
    	numberOfRepsText.setText("Reps = " + String.valueOf(repCount));
    }
    
    public void updateLastSet() {
    	lastDbRepEntry.setText("Program Finished - Set: " + id + " Reps: " + repDisplay);
    }
}