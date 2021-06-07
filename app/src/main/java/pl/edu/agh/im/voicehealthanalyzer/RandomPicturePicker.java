package pl.edu.agh.im.voicehealthanalyzer;

import java.util.Random;

public class RandomPicturePicker {

    private final Random r;

    private final Integer[] pictures = {
            R.drawable.picture0,
            R.drawable.picture1,
            R.drawable.picture2,
            R.drawable.picture3,
            R.drawable.picture4,
            R.drawable.picture5,
            R.drawable.picture6,
            R.drawable.picture7,
            R.drawable.picture8
    };

    public RandomPicturePicker() {
        this.r = new Random();
    }

    Integer getRandomPictureId() {
        return pictures[r.nextInt(pictures.length)];
    }
}
