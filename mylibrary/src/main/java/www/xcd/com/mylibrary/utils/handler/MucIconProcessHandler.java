package www.xcd.com.mylibrary.utils.handler;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;

import com.yonyou.sns.im.cache.bitmap.ProcessBitmapHandler;
import com.yonyou.sns.im.core.YYIMChatManager;
import com.yonyou.sns.im.entity.YYChatGroupMember;
import com.yonyou.sns.im.entity.YYChatGroupMemberList;
import com.yonyou.sns.im.zxing.entity.YYQrcodeInfo;

import java.util.ArrayList;
import java.util.List;

import www.xcd.com.mylibrary.R;
import www.xcd.com.mylibrary.utils.BitmapUtils;


/**
 * 房间头像处理类
 *
 * @author wudl
 */
public class MucIconProcessHandler extends ProcessBitmapHandler {
    /** 最大头像数量 */
    public static final int MAX_ICON_NUM = 4;
    /** icon 的 白色 padding 10*/
    private static final int ICON_WHITE_PADDING = 10;
    /** 成员默认头像 */
    public final int defaultIcon = R.mipmap.icon_default_user;
    /** 目标图片宽度 */
    protected int mImageWidth;
    /** 目标图片高度 */
    protected int mImageHeight;
    /** 上下文 */
    protected Context context;

    private BitmapCacheManager avatarCacheManager;

    /**
     * 初始化一个目标提供图像的宽度和高度的来处理图像
     *
     * @param context
     * @param imageWidth
     * @param imageHeight
     */
    public MucIconProcessHandler(Context context, int imageWidth, int imageHeight) {
        this.context = context;
        this.mImageWidth = imageWidth;
        this.mImageHeight = imageHeight;
        avatarCacheManager = new BitmapCacheManager(context, true, BitmapCacheManager.CIRCLE_BITMAP, BitmapCacheManager.BITMAP_DPSIZE_40);
        avatarCacheManager.generateBitmapCacheWork();
    }

    @Override
    public void initDiskCacheInternal() {

    }

    @Override
    public void clearCacheInternal() {

    }

    @Override
    public void flushCacheInternal() {

    }

    @Override
    public void closeCacheInternal() {

    }

    @Override
    protected Bitmap processBitmap(Object data) {
        // 房间头像
        Bitmap mucIcon = null;
        boolean withText = false;
        if (data instanceof String) {
            mucIcon  = processStringData((String) data,withText);
        }else if(data instanceof YYQrcodeInfo){
            mucIcon = processYYQrcodeInfoData((YYQrcodeInfo) data,withText);
        }
        return mucIcon;
    }

    /**
     * 处理是String 类型的data
     * @param info
     * @param withText
     * @return
     */
    private Bitmap processYYQrcodeInfoData(YYQrcodeInfo info , boolean withText){
        // 成员头像列表
        List<Bitmap> membersIcon = new ArrayList<Bitmap>();
        // 取房间成员的前三个用于生成群图片
        List<YYQrcodeInfo.ItemsEntity> membres = info.getItems();
        for (int i = 0; i < membres.size(); ) {
            if (i > MAX_ICON_NUM - 1) {
                break;
            }
            YYQrcodeInfo.ItemsEntity member = membres.get(i);
            // 获取头像资源
            byte[] buffers = avatarCacheManager.syncLoadFormCache(member.getPhoto(), member.getName());
            Bitmap bitmap;
            if (buffers == null || buffers.length <= 0) {
                bitmap = ResizerBitmapHandler.decodeSampledBitmapFromResource(context.getResources(), defaultIcon, mImageWidth,
                        mImageHeight);
            } else {
                bitmap = BitmapFactory.decodeByteArray(buffers, 0, buffers.length);
            }

            // 创建指定大小的图片
            bitmap = Bitmap.createScaledBitmap(bitmap, mImageWidth, mImageHeight, false);
            bitmap = BitmapUtils.toRoundBitmap(bitmap, ICON_WHITE_PADDING);
            // 不为空就加入到成员头像集合
            membersIcon.add(bitmap);
            i++;
        }
        Bitmap mucIcon = null;
        // 合成群头像
        if (membersIcon.size() > 0) {
            // 绘制群图标
            mucIcon = combineBitmaps(2, membersIcon, withText);
        }
        return mucIcon;
    }

    /**
     * 处理是String 类型的data
     * @param id
     * @param withText
     * @return
     */
    private Bitmap processStringData(String id , boolean withText){
        // 查询房间成员
        YYChatGroupMemberList memberList  = YYIMChatManager.getInstance().getChatGroupMemberByid(id, MAX_ICON_NUM);
        if (memberList==null||"".equals(memberList)){
            return null;
        }
        // 成员头像列表
        List<Bitmap> membersIcon = new ArrayList<Bitmap>();
        // 取房间成员的前三个用于生成群图片
        List<YYChatGroupMember> membres = memberList.getList();
        if (membres!=null&&membres.size()>0){
            for (int i = 0; i < membres.size(); ) {
                if (i > MAX_ICON_NUM - 1) {
                    break;
                }
                YYChatGroupMember member = membres.get(i);
                // 获取头像资源
                byte[] buffers = avatarCacheManager.syncLoadFormCache(member.getIcon(), member.getName());
                Bitmap bitmap;
                if (buffers == null || buffers.length <= 0) {
                    bitmap = ResizerBitmapHandler.decodeSampledBitmapFromResource(context.getResources(), defaultIcon, mImageWidth,
                            mImageHeight);
                } else {
                    bitmap = BitmapFactory.decodeByteArray(buffers, 0, buffers.length);
                }

                // 创建指定大小的图片
                bitmap = Bitmap.createScaledBitmap(bitmap, mImageWidth, mImageHeight, false);
                bitmap = BitmapUtils.toRoundBitmap(bitmap, ICON_WHITE_PADDING);
                // 不为空就加入到成员头像集合
                membersIcon.add(bitmap);
                i++;
            }
        }

        Bitmap mucIcon = null;
        // 合成群头像
        if (membersIcon!=null&&membersIcon.size() > 0) {
            // 绘制群图标
            mucIcon = combineBitmaps(2, membersIcon, withText);
        }
        return mucIcon;
    }

    @Override
    protected Bitmap onError(Object error) {
        return null;
    }

    /**
     * 绘制群组最后一个圆图形
     *
     * @param num 群组的成员数
     * @return
     */
    private Bitmap createLastIcon(String num, int width, int height, int padding) {
        Bitmap newBitmap = Bitmap.createBitmap(width + padding * 2, height + padding * 2, Config.ARGB_8888);
        Canvas canvas = new Canvas(newBitmap);
//        String color = BitmapUtils.colorMap.get(PinYinUtil.converterFromPinYinFirstLetter(String.valueOf(Integer.parseInt(num) % 10)));
//        if (TextUtils.isEmpty(color)) {
//            color = BitmapUtils.colorMap.get("def");
//        }
        canvas.drawColor(Color.parseColor("#00000000"));
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.parseColor("#BFCDD9"));
        int roundPx = newBitmap.getWidth() / 2;
        canvas.drawCircle(roundPx,roundPx,width /2+6,paint);
        paint.setColor(Color.WHITE);
        paint.setTextSize(width / 2);
        Paint.FontMetricsInt fontMetrics = paint.getFontMetricsInt();
        int baseline = (newBitmap.getHeight() - fontMetrics.bottom + fontMetrics.top) / 2 - fontMetrics.top;
        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(num, newBitmap.getWidth() / 2, baseline, paint);
        // 绘制外层白圈
        Paint mBorderPaint = new Paint();
        mBorderPaint.setAntiAlias(true);
        mBorderPaint.setColor(Color.WHITE);
        mBorderPaint.setXfermode(new PorterDuffXfermode(Mode.DST_OVER));
        canvas.drawCircle(roundPx, roundPx, roundPx, mBorderPaint);
        return newBitmap;
    }

    /**
     * 将多个Bitmap合并成一个图片。
     *
     * @param columns 将多个图合成多少列
     * @param bitmaps 要合成的图片
     * @return
     */
    public static Bitmap combineBitmaps(int columns, List<Bitmap> bitmaps, boolean withText) {
        if (columns <= 0 || bitmaps == null || bitmaps.size() == 0) {
            throw new IllegalArgumentException("Wrong parameters: columns must > 0 and bitmaps.length must > 0.");
        }
        int maxWidthPerImage = 0;
        int maxHeightPerImage = 0;
        for (Bitmap b : bitmaps) {
            maxWidthPerImage = maxWidthPerImage > b.getWidth() ? maxWidthPerImage : b.getWidth();
            maxHeightPerImage = maxHeightPerImage > b.getHeight() ? maxHeightPerImage : b.getHeight();
        }
        int rows = 0;
        // 列数不能超过总大小
        if (columns >= bitmaps.size()) {
            columns = bitmaps.size();
        }
        Bitmap newBitmap = null;
        if (bitmaps.size() == 1){
            newBitmap = bitmaps.get(0);
        }else if (bitmaps.size() == 2) {
            newBitmap = drawTowIcon(bitmaps.get(0), bitmaps.get(1), maxWidthPerImage * columns -30- 3*ICON_WHITE_PADDING);
        } else if (bitmaps.size() == 3) {
            newBitmap = drawThreeIcon(bitmaps.get(0), bitmaps.get(1), bitmaps.get(2), maxWidthPerImage * columns -10- 3*ICON_WHITE_PADDING);
        } else{
            newBitmap = drawFourIcon(bitmaps.get(0), bitmaps.get(1), bitmaps.get(2), bitmaps.get(3), maxWidthPerImage * columns-ICON_WHITE_PADDING, withText);
        }
        return newBitmap;
    }

    /**
     * 绘制四个人的头像
     *
     * @param first
     * @param second
     * @param third
     * @param forth
     * @param canvasWidth
     * @return
     */
    private static Bitmap drawFourIcon(Bitmap first, Bitmap second, Bitmap third, Bitmap forth, int canvasWidth, boolean withText) {
        Bitmap newBitmap = Bitmap.createBitmap(canvasWidth, canvasWidth, Config.ARGB_8888);
        try {
            Canvas canvas = new Canvas(newBitmap);
            Paint paint = new Paint();
            // 设置相交模式
            paint.setXfermode(new PorterDuffXfermode(Mode.SRC_OVER));
            // 绘制第一张icon全部
            Rect src = new Rect(0, 0, first.getWidth(), second.getHeight());
            // 绘制第二张icon全部
            Rect src2 = new Rect(0, 0, second.getWidth(), second.getHeight());
            // 绘制第三张icon全部
            Rect src3 = new Rect(0, 0, third.getWidth(), third.getHeight());
            // 绘制第四张icon全部
            Rect src4 = new Rect(0, 0, forth.getWidth(), forth.getHeight());
            // 第一张图绘制在左上角
            Rect rect = new Rect(0, 0, first.getWidth(), first.getHeight());
            // 第二张绘制在右上角
            Rect rect2 = new Rect(canvasWidth - second.getWidth(), 0, canvasWidth, second.getHeight());
            // 第三张绘制在左下角
            Rect rect3 = new Rect(0, canvasWidth - third.getHeight(), third.getWidth(), canvasWidth);
            // 第四张绘制在右下角
            Rect rect4 = new Rect(canvasWidth - forth.getWidth() + (withText ? 8 : 0), canvasWidth - forth.getHeight() + (withText ? 8 : 0), canvasWidth, canvasWidth);
            canvas.drawBitmap(forth, src4, rect4, paint);
            canvas.drawBitmap(third, src3, rect3, paint);
            canvas.drawBitmap(second, src2, rect2, paint);
            canvas.drawBitmap(first, src, rect, paint);
        } finally {
            first.recycle();
            second.recycle();
            third.recycle();
            forth.recycle();
        }
        return newBitmap;
    }

    /**
     * 绘制三个人头像
     *
     * @param first
     * @param second
     * @param third
     * @param canvasWidth
     * @return
     */
    private static Bitmap drawThreeIcon(Bitmap first, Bitmap second, Bitmap third, int canvasWidth) {
        Bitmap newBitmap = Bitmap.createBitmap(canvasWidth, canvasWidth, Config.ARGB_8888);
        try {
            Canvas canvas = new Canvas(newBitmap);
            Paint paint = new Paint();
            // 设置相交模式
            paint.setXfermode(new PorterDuffXfermode(Mode.SRC_OVER));
            // 绘制第一张icon全部
            Rect src = new Rect(0, 0, first.getWidth(), first.getHeight());
            // 绘制第二张icon全部
            Rect src2 = new Rect(0, 0, second.getWidth(), second.getHeight());
            // 绘制第三张icon全部
            Rect src3 = new Rect(0, 0, third.getWidth(), third.getHeight());
            // 第一张图绘制第一行中间
            int width = (canvasWidth - first.getWidth()) / 2;
            Rect rect = new Rect(width, 0, width + first.getWidth(), first.getHeight());
            // 第二张绘制在左下角
            Rect rect2 = new Rect(0, canvasWidth - second.getHeight(), second.getWidth(), canvasWidth);
            // 第三张绘制在右下角
            Rect rect3 = new Rect(canvasWidth - third.getWidth(), canvasWidth - third.getHeight(), canvasWidth, canvasWidth);
            canvas.drawBitmap(second, src2, rect2, paint);
            canvas.drawBitmap(third, src3, rect3, paint);
            canvas.drawBitmap(first, src, rect, paint);
        } finally {
            first.recycle();
            second.recycle();
            third.recycle();
        }
        return newBitmap;
    }

    /**
     * 绘制俩个人的图标
     *
     * @param first
     * @param second
     * @param canvasWidth
     * @return
     */
    private static Bitmap drawTowIcon(Bitmap first, Bitmap second, int canvasWidth) {
        Bitmap newBitmap = Bitmap.createBitmap(canvasWidth, canvasWidth, Config.ARGB_8888);
        try {
            Canvas cv = new Canvas(newBitmap);
            Paint paint = new Paint();
            // 设置相交模式
            paint.setXfermode(new PorterDuffXfermode(Mode.SRC_OVER));
            // 绘制第一张图的全部
            Rect src = new Rect(0, 0, first.getWidth(), first.getHeight());
            // 绘制第二张图的全部
            Rect src2 = new Rect(0, 0, second.getWidth(), second.getHeight());
            // 第一张图在左上角绘制
            RectF rect = new RectF(canvasWidth - second.getWidth(), 0, canvasWidth, second.getHeight());
            // 第二张图在右下角绘制
            RectF rect2 = new RectF(0, canvasWidth - second.getHeight(), second.getWidth(), canvasWidth);
            cv.drawBitmap(first, src, rect, paint);
            cv.drawBitmap(second, src2, rect2, paint);
        } finally {
            first.recycle();
            second.recycle();
        }
        return newBitmap;
    }

    /**
     * 将位图变成透明
     *
     * @param sourceImg
     * @param number number的范围是0-100，0表示完全透明即完全看不到
     * @return
     */
    public static Bitmap getTransparentBitmap(Bitmap sourceImg, int number) {
        int[] argb = new int[sourceImg.getWidth() * sourceImg.getHeight()];

        sourceImg.getPixels(argb, 0, sourceImg.getWidth(), 0, 0, sourceImg

                .getWidth(), sourceImg.getHeight());// 获得图片的ARGB值

        number = number * 255 / 100;

        for (int i = 0; i < argb.length; i++) {

            argb[i] = (number << 24) | (argb[i] & 0x00FFFFFF);

        }

        sourceImg = Bitmap.createBitmap(argb, sourceImg.getWidth(), sourceImg

                .getHeight(), Config.ARGB_8888);

        return sourceImg;
    }

    /**
     * Mix two Bitmap as one.
     *
     * @return
     */
    public static Bitmap mixtureBitmap(Bitmap first, Bitmap second, PointF fromPoint) {
        if (first == null || second == null || fromPoint == null) {
            return null;
        }
        Bitmap newBitmap = Bitmap.createBitmap(first.getWidth(), first.getHeight(), Config.ARGB_8888);
        Canvas cv = new Canvas(newBitmap);
        cv.drawColor(Color.parseColor("#ffffffff"));
        Paint paint = new Paint();
        // 设置相交模式
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_OVER));
        Rect src = new Rect(0, 0, first.getWidth(), first.getHeight());
        RectF rect = new RectF(fromPoint.x, fromPoint.y, fromPoint.x + second.getWidth(), fromPoint.y + second.getHeight());
        Rect src2 = new Rect(0, 0, second.getWidth(), second.getHeight());
        cv.drawBitmap(first, src, src, paint);
        cv.drawBitmap(second, src2, rect, paint);
        return newBitmap;
    }

    /**
     * 获取成员url
     *
     * @return
     */
    protected String getMemUrl(YYChatGroupMember member) {
        return member.getIcon();
    }
}
