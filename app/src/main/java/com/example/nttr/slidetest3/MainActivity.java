package com.example.nttr.slidetest3;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity { //implements View.OnTouchListener {

    // タッチイベントを処理するためのインタフェース
    private GestureDetector mGestureDetector;

    // スライドエリアの縦横のマス数
    private int mPieceX = 4;
    private int mPieceY = 4; // 原則同数にする。異なる場合、各マスが長方形になる

    // レイアウト関連
    private final int PIECE_MARGIN = 15;    // 各マスのマージン

    // スライドエリアの各パネルをLinearLayoutで管理し、配列化
    ArrayList<ImageView> mImagePieces = new ArrayList<ImageView>();

    // 操作対象マスの管理用(-1は操作対象なし)
    private final int SELECT_NONE = -1;
    private int mPieceTag = SELECT_NONE;

    // フリック方向の定数
    final int DIRECTION_NONE   = 0;
    final int DIRECTION_TOP    = 1;
    final int DIRECTION_LEFT   = 2;
    final int DIRECTION_RIGHT  = 3;
    final int DIRECTION_BOTTOM = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // タッチイベントのインスタンスを生成
        mGestureDetector = new GestureDetector(this, mOnGestureListener);

        // LinearLayoutの入れ子
        // スライドエリア（親）は配置済
        LinearLayout llSlideArea = (LinearLayout) findViewById(R.id.llSlideArea);
        // 周辺の余白はマージン1個分のため、小さめ。調整するなら、親要素のpaddingの追加が必要か

        for(int i=0; i<mPieceX; i++) {

            // 子（1行分のLinearLayout）の生成
            LinearLayout.LayoutParams lpRow
                    =   new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    0);
            lpRow.weight = 1.0f;
            LinearLayout llRow = new LinearLayout(this);
            llRow.setOrientation(LinearLayout.HORIZONTAL);
            llRow.setLayoutParams(lpRow);
            llRow.setGravity(Gravity.CENTER_VERTICAL);

            llSlideArea.addView(llRow);

            for(int j=0; j<mPieceY; j++) {

                // 孫（各パネル）の生成
                LinearLayout.LayoutParams lpPiece
                        =   new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                lpPiece.setMargins(PIECE_MARGIN,PIECE_MARGIN,PIECE_MARGIN,PIECE_MARGIN);
                lpPiece.weight = 1.0f;
                ImageView iv = new ImageView(this);
                iv.setLayoutParams(lpPiece);
                iv.setImageResource(R.drawable.hoshi);
                iv.setScaleType(ImageView.ScaleType.FIT_XY);
                //iv.setBackgroundColor(Color.BLUE);
                //iv.setOnTouchListener(this);

                // https://akira-watson.com/android/button-array.html
                //iv.setTag("ImageView-" + String.valueOf(i*mPieceY+j)); // 多分、1次元配列上の添え字
                iv.setTag(String.valueOf(i*mPieceY+j)); // 多分、1次元配列上の添え字

                // 親へ追加
                llRow.addView(iv);

                // リストに追加
                mImagePieces.add(iv);

            } //for j

        } //for i

    }

//    @Override
//    public boolean onTouch(View v, MotionEvent event) {
//        //Toast toast = new Toast();
//
//        // フリックのスタート位置となる、タッチされたImageButtonのタグを保持
//        // 文字列→整数変換
//        // http://www.adakoda.com/android/000108.html
//        int tag;
//        // 整数でないタグの場合、java.NumberFormatExceptionが発生するので回避する
//        try {
//            tag = Integer.parseInt(v.getTag().toString());
//        }
//        catch (Exception e) {
//            tag = SELECT_NONE;
//        }
//
//        if ((SELECT_NONE < tag) && (tag < mPieceX * mPieceY)) {
//            // 有効な整数であれば保持
//            // OnTouchリスナーを追加した場合を想定
//            mPieceTag = tag;
//        } else {
//            // 有効でなければクリア
//            mPieceTag = SELECT_NONE;
//        }
//        Log.d("onTouch", String.valueOf(mPieceTag));
//
////        // タッチされたImageButtonの画像を消す
////        // ArrayListの要素取り出し
////        // https://www.javadrive.jp/start/arraylist/index2.html
////        // ImageResource消す
////        // http://study.tuffyz.com/study/2017/01/11/android-imageview%E3%81%AB%E8%A8%AD%E5%AE%9A%E3%81%97%E3%81%9F%E7%94%BB%E5%83%8F%E3%82%92%E6%B6%88%E3%81%99%E3%81%AB%E3%81%AF/
////        mImagePieces.get(mPieceTag).setImageDrawable(null);
//
//        // 未検証
//        // ImageViewから画像取り出し
//        // http://falco.sakura.ne.jp/tech/2013/09/android-imageview-%E3%82%88%E3%82%8A-bitmap-%E3%82%92%E5%8F%96%E5%BE%97%E3%81%99%E3%82%8B%E3%81%AB%E3%81%AF%EF%BC%9F/
//
//        // get/set ImageBitmap, ImageDrawable >= ImageResources
//        // https://stackoverflow.com/questions/9774705/setimageresource-vs-setdrawable
//
//        return mGestureDetector.onTouchEvent(event);
//    }

    // タッチイベント
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mGestureDetector.onTouchEvent(event);
    }

    // タッチイベントのリスナー
    private final GestureDetector.SimpleOnGestureListener mOnGestureListener
             = new GestureDetector.SimpleOnGestureListener() {

        // フリックイベント
        @Override
        public boolean onFling(MotionEvent event1, MotionEvent event2, float velocityX, float velocityY) {

            final int SWIPE_MIN_DISTANCE = 50;          // 最小移動距離
            final int SWIPE_THRESHOLD_VELOCITY = 200;   // 最小速度
            final float SLOPE_RATE = 1.1f;              // 斜めと判定されないための比率

            // フリック方向
            int directionX = DIRECTION_NONE;
            int directionY = DIRECTION_NONE;
            int direction  = DIRECTION_NONE;

            try {

                // どのView上かチェック
                int viewIndex = getViewIndex(event1.getX(), event1.getY());
                if (SELECT_NONE < viewIndex && viewIndex < mImagePieces.size()) {
                    mPieceTag = viewIndex;
                } else {
                    return false;
                }

                // 移動距離・スピードを出力
                float distance_x = event2.getX() - event1.getX();
//                float velocity_x = Math.abs(velocityX);
                float distance_y = event1.getY() - event2.getY();
//                float velocity_y = Math.abs(velocityY);
                String strX = null;
                String strY = null;

                // 左右確認
                // 開始位置から終了位置の移動距離が指定値より大きい
                // X軸の移動速度が指定値より大きい
                if (distance_x < -SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    strX = "左";
                    directionX = DIRECTION_LEFT;

                // 終了位置から開始位置の移動距離が指定値より大きい
                // X軸の移動速度が指定値より大きい
                } else if (distance_x > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    strX = "右";
                    directionX = DIRECTION_RIGHT;

                }

                // Y軸の移動速度が指定値より大きい
                if (distance_y > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
                    strY = "上";
                    directionY = DIRECTION_TOP;

                } else if (distance_y < -SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
                    strY = "下";
                    directionY = DIRECTION_BOTTOM;

                }

                // 方向（上記の合成？）
                // event1:移動開始 event2:移動終了
                // X軸は、右が大きい。Y軸は下が大きい。
                if (Math.abs(distance_x) > Math.abs(distance_y) * SLOPE_RATE) {
                    // X方向が大きい
                    Log.d("onFling", mPieceTag + "が" + strX + "方向");
                    direction = directionX;

                } else if (Math.abs(distance_x) * SLOPE_RATE < Math.abs(distance_y)) {
                    // Y方向が大きい
                    Log.d("onFling",mPieceTag + "が" + strY + "方向");
                    direction = directionY;

                } else {
                    // ほぼ等しいは無視扱い
                    Log.d("onFling", "ほぼ、斜め" + strX + strY + "方向");
                    direction = DIRECTION_NONE;

                }

            } catch (Exception e) {
                // TODO
            }

            // 移動アニメーション
            movePiece(mPieceTag, direction);

            mPieceTag = SELECT_NONE;

            return false;
        }
    };

    private int getViewIndex(float flingX, float flingY) {
        int index = SELECT_NONE;
        final int padding = 0;

        ImageView iv;

        for (int i=0; i<mImagePieces.size(); i++) {
            iv = mImagePieces.get(i);
            // Viewの座標の取得
            // http://rounin.biz/programming/358/
            // 判定の度に取得してたら重い？もったいない？
            int[] lo = new int[2];
            iv.getLocationInWindow(lo);
            int ivLeft = lo[0];
            int ivTop = lo[1];
            int ivRight = ivLeft + iv.getWidth();
            int ivBottom = ivTop + iv.getHeight();

            // 判定を厳しくする(余白の内側のみにする)
            // ※余白は、Viewのpaddingプロパティにするのもあり
            ivLeft   += padding;
            ivTop    += padding;
            ivRight  -= padding;
            ivBottom -= padding;

            // マイナス(矛盾)を補正しないのは、次のif文が成立しないため

            // 引数の座標がこのView内であるか判定
            if (ivLeft < flingX && flingX < ivRight
                    && ivTop < flingY && flingY < ivBottom ) {
                Log.d("onFling", "In ImageView-" + i);
                // Viewが見つかったのでループ中断
                index = i;
                break;
            } //if

        } //for i
        return index;
    }

    void movePiece(int viewIndex, int direction ) {

        ImageView iv = mImagePieces.get(viewIndex);

        switch (direction) {
            case DIRECTION_TOP:
                // 上方向へ移動

                // プロパティアニメーションでは難しそう。特に、移動した後戻ってこない？(Animationオブジェクト要？)
                // 180118: 上に移動し、他のLinearLayoutの裏に回る？

                // 一番前？→一番右に移動してアニメーション。同Layout内で、一番右が一番上と思われる
                // https://qiita.com/amay077/items/fa7fa1f4be4243a91567
                //iv.bringToFront();

                // 2秒待って、上へ移動しながら消える。分割しても変化なし
                //iv.animate().setDuration(2000).translationY(-30);
                //iv.setStartDelay(2000);
                //iv.animate().alpha(0).setDuration(2000);
                //iv.animate().setDuration(2000).translationY(0);
                //iv.setImageDrawable(null); //元位置の画像は消す



                break;
            case DIRECTION_LEFT:
                break;
            case DIRECTION_RIGHT:
                break;
            case DIRECTION_BOTTOM:
                break;
        }

    }
}
