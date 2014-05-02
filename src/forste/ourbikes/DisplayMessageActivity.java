package forste.ourbikes;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class DisplayMessageActivity extends Activity {
	  View mVictimContainer;
	  View mVictim1;
	  View mVictim2;

	  @Override
	  protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);

	    setContentView(R.layout.surface_view_overlay);

	    GLSurfaceView glSurfaceView = (GLSurfaceView) findViewById(R.id.glsurfaceview);
	    glSurfaceView.setRenderer(new CubeRenderer(false));

	    // Find the views whose visibility will change
	    mVictimContainer = findViewById(R.id.hidecontainer);
	    mVictim1 = findViewById(R.id.hideme1);
	    mVictim1.setOnClickListener(new HideMeListener(mVictim1));
	    mVictim2 = findViewById(R.id.hideme2);
	    mVictim2.setOnClickListener((OnClickListener) new HideMeListener(mVictim2));

	    // Find our buttons
	    Button visibleButton = (Button) findViewById(R.id.vis);
	    Button invisibleButton = (Button) findViewById(R.id.invis);
	    Button goneButton = (Button) findViewById(R.id.gone);

	    // Wire each button to a click listener
//	    visibleButton.setOnClickListener(mVisibleListener);
//	    invisibleButton.setOnClickListener(mInvisibleListener);
//	    goneButton.setOnClickListener(mGoneListener);
	  }

	  @Override
	  protected void onResume() {
	    // Ideally a game should implement onResume() and onPause()
	    // to take appropriate action when the activity looses focus
	    super.onResume();
	  }

	  @Override
	  protected void onPause() {
	    // Ideally a game should implement onResume() and onPause()
	    // to take appropriate action when the activity looses focus
	    super.onPause();
	  }

	  class HideMeListener implements OnClickListener {
	    final View mTarget;

	    HideMeListener(View target) {
	      mTarget = target;
	    }

	    public void onClick(View v) {
	      mTarget.setVisibility(View.INVISIBLE);
	    }

	  }

	  OnClickListener mVisibleListener = new OnClickListener() {
	    public void onClick(View v) {
	      mVictim1.setVisibility(View.VISIBLE);
	      mVictim2.setVisibility(View.VISIBLE);
	      mVictimContainer.setVisibility(View.VISIBLE);
	    }
	  };

	  OnClickListener mInvisibleListener = new OnClickListener() {
	    public void onClick(View v) {
	      mVictim1.setVisibility(View.INVISIBLE);
	      mVictim2.setVisibility(View.INVISIBLE);
	      mVictimContainer.setVisibility(View.INVISIBLE);
	    }
	  };

	  OnClickListener mGoneListener = new OnClickListener() {
	    public void onClick(View v) {
	      mVictim1.setVisibility(View.GONE);
	      mVictim2.setVisibility(View.GONE);
	      mVictimContainer.setVisibility(View.GONE);
	    }
	  };
	}

	/**
	 * A vertex shaded cube.
	 */
	class Cube {
	  public Cube() {
	    int one = 0x10000;
	    int vertices[] = { -one, -one, -one, one, -one, -one, one, one, -one,
	        -one, one, -one, -one, -one, one, one, -one, one, one, one,
	        one, -one, one, one, };

	    int colors[] = { 0, 0, 0, one, one, 0, 0, one, one, one, 0, one, 0,
	        one, 0, one, 0, 0, one, one, one, 0, one, one, one, one, one,
	        one, 0, one, one, one, };

	    byte indices[] = { 0, 4, 5, 0, 5, 1, 1, 5, 6, 1, 6, 2, 2, 6, 7, 2, 7,
	        3, 3, 7, 4, 3, 4, 0, 4, 7, 6, 4, 6, 5, 3, 0, 1, 3, 1, 2 };

	    // Buffers to be passed to gl*Pointer() functions
	    // must be direct, i.e., they must be placed on the
	    // native heap where the garbage collector cannot
	    // move them.
	    //
	    // Buffers with multi-byte datatypes (e.g., short, int, float)
	    // must have their byte order set to native order

	    ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
	    vbb.order(ByteOrder.nativeOrder());
	    mVertexBuffer = vbb.asIntBuffer();
	    mVertexBuffer.put(vertices);
	    mVertexBuffer.position(0);

	    ByteBuffer cbb = ByteBuffer.allocateDirect(colors.length * 4);
	    cbb.order(ByteOrder.nativeOrder());
	    mColorBuffer = cbb.asIntBuffer();
	    mColorBuffer.put(colors);
	    mColorBuffer.position(0);

	    mIndexBuffer = ByteBuffer.allocateDirect(indices.length);
	    mIndexBuffer.put(indices);
	    mIndexBuffer.position(0);
	  }

	  public void draw(GL10 gl) {
	    gl.glFrontFace(GL10.GL_CW);
	    gl.glVertexPointer(3, GL10.GL_FIXED, 0, mVertexBuffer);
	    gl.glColorPointer(4, GL10.GL_FIXED, 0, mColorBuffer);
	    gl.glDrawElements(GL10.GL_TRIANGLES, 36, GL10.GL_UNSIGNED_BYTE,
	        mIndexBuffer);
	  }

	  private IntBuffer mVertexBuffer;
	  private IntBuffer mColorBuffer;
	  private ByteBuffer mIndexBuffer;
	}

	/**
	 * Render a pair of tumbling cubes.
	 */

	class CubeRenderer implements GLSurfaceView.Renderer {
	  public CubeRenderer(boolean useTranslucentBackground) {
	    mTranslucentBackground = useTranslucentBackground;
	    mCube = new Cube();
	  }

	  public void onDrawFrame(GL10 gl) {
	    /*
	     * Usually, the first thing one might want to do is to clear the screen.
	     * The most efficient way of doing this is to use glClear().
	     */

	    gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

	    /*
	     * Now we're ready to draw some 3D objects
	     */

	    gl.glMatrixMode(GL10.GL_MODELVIEW);
	    gl.glLoadIdentity();
	    gl.glTranslatef(0, 0, -3.0f);
	    gl.glRotatef(mAngle, 0, 1, 0);
	    gl.glRotatef(mAngle * 0.25f, 1, 0, 0);

	    gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
	    gl.glEnableClientState(GL10.GL_COLOR_ARRAY);

	    mCube.draw(gl);

	    gl.glRotatef(mAngle * 2.0f, 0, 1, 1);
	    gl.glTranslatef(0.5f, 0.5f, 0.5f);

	    mCube.draw(gl);

	    mAngle += 1.2f;
	  }

	  public void onSurfaceChanged(GL10 gl, int width, int height) {
	    gl.glViewport(0, 0, width, height);

	    /*
	     * Set our projection matrix. This doesn't have to be done each time we
	     * draw, but usually a new projection needs to be set when the viewport
	     * is resized.
	     */

	    float ratio = (float) width / height;
	    gl.glMatrixMode(GL10.GL_PROJECTION);
	    gl.glLoadIdentity();
	    gl.glFrustumf(-ratio, ratio, -1, 1, 1, 10);
	  }

	  public void onSurfaceCreated(GL10 gl, EGLConfig config) {
	    /*
	     * By default, OpenGL enables features that improve quality but reduce
	     * performance. One might want to tweak that especially on software
	     * renderer.
	     */
	    gl.glDisable(GL10.GL_DITHER);

	    /*
	     * Some one-time OpenGL initialization can be made here probably based
	     * on features of this particular context
	     */
	    gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_FASTEST);

	    if (mTranslucentBackground) {
	      gl.glClearColor(0, 0, 0, 0);
	    } else {
	      gl.glClearColor(1, 1, 1, 1);
	    }
	    gl.glEnable(GL10.GL_CULL_FACE);
	    gl.glShadeModel(GL10.GL_SMOOTH);
	    gl.glEnable(GL10.GL_DEPTH_TEST);
	  }

	  private boolean mTranslucentBackground;
	  private Cube mCube;
	  private float mAngle;

	}
