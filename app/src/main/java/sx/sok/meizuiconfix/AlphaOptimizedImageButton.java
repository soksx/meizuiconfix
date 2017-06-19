package sx.sok.meizuiconfix;

/**
 * Created by sokk on 18/06/2017.
 */

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
/**
 * A frame layout which does not have overlapping renderings commands and therefore does not need a
 * layer when alpha is changed.
 */
public class AlphaOptimizedImageButton extends android.support.v7.widget.AppCompatImageButton {
    public AlphaOptimizedImageButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    @Override
    public boolean hasOverlappingRendering() {
        return false;
    }
}
