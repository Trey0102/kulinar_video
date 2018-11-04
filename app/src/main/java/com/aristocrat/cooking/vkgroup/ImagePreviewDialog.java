package com.aristocrat.cooking.vkgroup;

import android.annotation.SuppressLint;

@SuppressLint("NewApi")
public class ImagePreviewDialog/* extends Dialog*/{
/*
	Bitmap bitmap = null;
	ImageView img;
	ZoomControls zoom;
	public ImagePreviewDialog(Context context) {
		super(context);
		requestWindowFeature(Window.FEATURE_NO_TITLE);     
		setContentView(R.layout.image_preview_fragment);
		img = (ImageView)findViewById(R.id.preview_img);
		zoom = (ZoomControls)findViewById(R.id.zoom_control);
	}
	public void setImageResource(Bitmap bitmap)
	{
		this.bitmap = bitmap;
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		final int screenWidth = this.getWindow().getWindowManager().getDefaultDisplay().getWidth();
		final int screenHeight = this.getWindow().getWindowManager().getDefaultDisplay().getHeight();
						
		if (bitmap != null)
		{
			img.setImageBitmap(bitmap);
			img.setLayoutParams(new LinearLayout.LayoutParams(screenWidth, screenHeight));
//			//set maximum scroll amount (based on center of image)
//			img.setOnTouchListener(new View.OnTouchListener() {
//
//				float mx= 0, my=0;
//		        public boolean onTouch(View arg0, MotionEvent event) {
//
//		            float curX, curY;
//		            int byX, byY;
//		            switch (event.getAction()) {
//
//		                case MotionEvent.ACTION_DOWN:
//		                    mx = event.getX();
//		                    my = event.getY();
//		                    break;
//		                case MotionEvent.ACTION_MOVE:
//		                    curX = event.getX();
//		                    curY = event.getY();
//		                    byX = (int) (mx - curX);		                    
//		                    byY = (int) (my - curY);
//		                    int imgWidth = (int)(img.getHeight()*img.getScaleX());
//		                    int t = img.getScrollX();
//		                    Log.d("limit", t + " " + (imgWidth + t) + " " + screenWidth);
//		                    if (t < 0 && imgWidth + t < screenWidth)
//		                    	byX = 0;
//		                    img.scrollBy(byX, byY);
//		                    mx = curX;
//		                    my = curY;
//		                    break;
//		                    
//		                case MotionEvent.ACTION_UP:
//		                    curX = event.getX();
//		                    curY = event.getY();
//		                    img.scrollBy((int) (mx - curX), (int) (my - curY));
//		                    break;
//		            }
//
//		            return true;
//		        }
//		    });
//			

		    // set touchlistener
		    img.setOnTouchListener(new View.OnTouchListener()
		    {
			    
		        float downX, downY;
		        int totalX, totalY;
		        int scrollByX, scrollByY;
		        
		        public boolean onTouch(View view, MotionEvent event)
		        {
					int maxX = (int)(img.getWidth()*img.getScaleX())/2 - screenWidth/2;
					if (maxX < 0)
						maxX = 0;
					int maxY = (int)(img.getHeight()*img.getScaleY())/2 - screenHeight/2;
					if (maxY < 0)
						maxY = 0;
        		    Log.d("limit", img.getWidth()*img.getScaleX() + " " + maxX + " " + img.getScrollX());
				   
				    // set scroll limits
				    final int maxLeft = (maxX * -1);
				    final int maxRight = maxX;
				    final int maxTop = (maxY * -1);
				    final int maxBottom = maxY;
				    
		            float currentX, currentY;
		            switch (event.getAction())
		            {
		                case MotionEvent.ACTION_DOWN:
		                    downX = event.getX();
		                    downY = event.getY();
		                    break;

		                case MotionEvent.ACTION_MOVE:
		                    currentX = event.getX();
		                    currentY = event.getY();
		                    scrollByX = (int)(downX - currentX);
		                    scrollByY = (int)(downY - currentY);
		                    // scrolling to left side of image (pic moving to the right)
		                    if (currentX > downX)
		                    {
		                        if (totalX == maxLeft)
		                        {
		                            scrollByX = 0;
		                        }
		                        if (totalX > maxLeft)
		                        {
		                            totalX = totalX + scrollByX;
		                        }
		                        if (totalX < maxLeft)
		                        {
		                            scrollByX = maxLeft - (totalX - scrollByX);
		                            totalX = maxLeft;
		                        }
		                    }

		                    // scrolling to right side of image (pic moving to the left)
		                    if (currentX < downX)
		                    {
		                        if (totalX == maxRight)
		                        {
		                            scrollByX = 0;
		                        }
		                        if (totalX < maxRight)
		                        {
		                            totalX = totalX + scrollByX;
		                        }
		                        if (totalX > maxRight)
		                        {
		                            scrollByX = maxRight - (totalX - scrollByX);
		                            totalX = maxRight;
		                        }
		                    }

		                    // scrolling to top of image (pic moving to the bottom)
		                    if (currentY > downY)
		                    {
		                        if (totalY == maxTop)
		                        {
		                            scrollByY = 0;
		                        }
		                        if (totalY > maxTop)
		                        {
		                            totalY = totalY + scrollByY;
		                        }
		                        if (totalY < maxTop)
		                        {
		                            scrollByY = maxTop - (totalY - scrollByY);
		                            totalY = maxTop;
		                        }
		                    }

		                    // scrolling to bottom of image (pic moving to the top)
		                    if (currentY < downY)
		                    {
		                        if (totalY == maxBottom)
		                        {
		                            scrollByY = 0;
		                        }
		                        if (totalY < maxBottom)
		                        {
		                            totalY = totalY + scrollByY;
		                        }
		                        if (totalY > maxBottom)
		                        {
		                            scrollByY = maxBottom - (totalY - scrollByY);
		                            totalY = maxBottom;
		                        }
		                    }

		                    img.scrollBy(scrollByX, scrollByY);
		                    downX = currentX;
		                    downY = currentY;
		                    break;

		            }

		            return true;
		        }
		    });
			
			zoom.setOnZoomInClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {

					float x = img.getScaleX();
					float y = img.getScaleY();
					
					img.setScaleX(x+0.1f);
					img.setScaleY(y+0.1f);
				}
			});
	        zoom.setOnZoomOutClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {

		 
					float x = img.getScaleX();
					float y = img.getScaleY();
					
					img.setScaleX(x-0.1f);
					img.setScaleY(y-0.1f);
				}
	        });
		}
	}
	
	@Override
	public void show() {
		super.show();
	}

*/
}
