package me.grocery.grocerylist;

import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.AnimationSet;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import me.grocery.grocerylist.ai.GroceryListConstructor;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {
    private final int SPLASH_DISPLAY_LENGTH = 8000;
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler handler = new Handler(Looper.getMainLooper());
    String[] mealPlans = {
            "1400 calorie keto meal plan",
            "1800 calorie vegan meal plan",
            "1200 calorie paleo meal plan",
            "1600 calorie vegetarian meal plan",
            "2000 calorie gluten-free meal plan",
            "1500 calorie low-carb meal plan"
    };
    String[] modelQuestions = {
            "Test 1",
            "Test 2",
            "Test 3",
            "Test 4",
            "Test 5",
    };
    private List<String> questions;

    public void setQuestions(List<String> questions) {
        this.questions = questions;
    }

    public List<String> getQuestions() {
        return questions;
    }

    private List<String> answers;

    public List<String> getAnswers() {
        return answers;
    }

    private String initialPrompt;
    private String userInput;
    public String getUserInput() {
        return userInput;
    }

    private JSONObject data;

    public JSONObject getData() {
        return data;
    }

    Animation fadeOut;
    private boolean isAnimationRunning;
    private Runnable typingAnimation;
    private boolean cursorBlinkState = false;
    private int textIndex = 0;
    private int charIndex = 0;
    private final long TYPING_INTERVAL = 150;
    private final long CURSOR_BLINK_INTERVAL = 500;
    private final long TEXT_PAUSE_DURATION = 2000;
    private Runnable cursorBlinkRunnable = null;
    private int DURATION = 2000;
    private int OFFSET = 1500;


    private int currentQuestionIndex = 0;
    private TextView textViewQuestion;
    private EditText editTextAnswer;
    private Button buttonSubmit;
    ImageView progressBar;

    ImageView logoImageView;
    TextView welcomeTextView;
    TextView titleTextView;
    TextView loadingTextView;
    EditText editText;
    Button submitButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        isAnimationRunning = true;
         logoImageView = findViewById(R.id.logoImageView);
         welcomeTextView = findViewById(R.id.welcomeText);
         initialPrompt = welcomeTextView.getText().toString();
         loadingTextView = findViewById(R.id.loadingTextView);
         titleTextView = findViewById(R.id.titleText);
         editText = findViewById(R.id.inputEditText);
         submitButton = findViewById(R.id.submitButton);
        progressBar = findViewById(R.id.loadingImageView);
        textViewQuestion =welcomeTextView;
        editTextAnswer = editText;
        buttonSubmit = submitButton;
        answers = new ArrayList<String>();

        Animation fadeInForLogo = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        Animation slideUpForLogo = AnimationUtils.loadAnimation(this, R.anim.slide_up);
        Animation fadeInForTitle = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        Animation slideUpForTitle = AnimationUtils.loadAnimation(this, R.anim.slide_up);
        Animation fadeInForText = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        Animation slideUpForText = AnimationUtils.loadAnimation(this, R.anim.slide_up);
        Animation fadeInForEditText = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        Animation rotate = AnimationUtils.loadAnimation(this, R.anim.rotate);

        Interpolator interpolator = AnimationUtils.loadInterpolator(this, R.anim.my_interpolator);
        slideUpForLogo.setInterpolator(interpolator);
        slideUpForText.setInterpolator(interpolator);
        slideUpForTitle.setInterpolator(interpolator);

        Intent intent = new Intent(this, MainActivity.class);
        SharedPreferences sharedPreferences = getSharedPreferences("shared", MODE_PRIVATE);
        File file = new File(this.getFilesDir(), "groceryItems.json");

        Log.d("groceryITEMSTEST:", file.toString());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isDisplayed", false);
        editor.apply();
        if(!file.exists() || file.length() != 0)
        {
            if (sharedPreferences.getBoolean("isDisplayed", false)) {
                Log.d("isDisplayed","FALSE");
                finish();
                startActivity(intent);
            }

        }
        ;
        editor.putBoolean("isDisplayed", true);
        editor.apply();



        fadeInForEditText.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                editText.setVisibility(View.VISIBLE);
            }

            final Handler handler = new Handler(Looper.getMainLooper());

            @Override
            public void onAnimationEnd(Animation animation) {
                animateEditTextHint(editText);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });


        Animation fadeInForButton = AnimationUtils.loadAnimation(this, R.anim.fade_in);
         fadeOut = AnimationUtils.loadAnimation(SplashActivity.this, R.anim.fade_out);

        AnimationSet logoAnimationSet = new AnimationSet(false);
        logoAnimationSet.addAnimation(slideUpForLogo);
        logoAnimationSet.addAnimation(fadeInForLogo);

        AnimationSet titleAnimationSet = new AnimationSet(false);
        titleAnimationSet.addAnimation(slideUpForTitle);
        titleAnimationSet.addAnimation(fadeInForTitle);

        AnimationSet textAnimationSet = new AnimationSet(false);
        textAnimationSet.addAnimation(slideUpForText);
        textAnimationSet.addAnimation(fadeInForText);

        logoAnimationSet.setDuration(DURATION);
        textAnimationSet.setDuration(DURATION);
        titleAnimationSet.setStartOffset(0);
        textAnimationSet.setStartOffset(1000);
        fadeInForEditText.setStartOffset(3000);
        fadeInForButton.setStartOffset(3500);

        logoImageView.setVisibility(View.VISIBLE);
        logoImageView.startAnimation(logoAnimationSet);

        titleTextView.setVisibility(View.VISIBLE);
        titleTextView.startAnimation(textAnimationSet);

        welcomeTextView.setVisibility(View.VISIBLE);
        welcomeTextView.startAnimation(textAnimationSet);

        editText.setVisibility(View.VISIBLE);
        editText.startAnimation(fadeInForEditText);

        submitButton.setVisibility(View.VISIBLE);
        submitButton.startAnimation(fadeInForButton);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                userInput = editText.getText().toString();
                if (userInput.equals("fake") || userInput.isEmpty()) {
                    Toast.makeText(SplashActivity.this, "Please provide a proper response.", Toast.LENGTH_LONG).show();
                    editTextAnswer.requestFocus();
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);
                progressBar.startAnimation(fadeInForLogo);
                progressBar.startAnimation(rotate);

                sendPromptToBackendAndReceiveQuestions(userInput);
                Log.d("user input:", userInput);

            }
        });


    }

    private void sendPromptToBackendAndReceiveQuestions(final String userInput) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Animation fadeIn = AnimationUtils.loadAnimation(SplashActivity.this, R.anim.fade_in);
                Animation rotate = AnimationUtils.loadAnimation(SplashActivity.this, R.anim.rotate);

                AnimationSet animationSet = new AnimationSet(true);
                animationSet.addAnimation(rotate);
                animationSet.addAnimation(fadeIn);

                progressBar.startAnimation(animationSet);

                logoImageView.startAnimation(animationSet);
                logoImageView.startAnimation(fadeOut);
                titleTextView.startAnimation(fadeOut);
                welcomeTextView.startAnimation(fadeOut);
                editText.startAnimation(fadeOut);
                submitButton.startAnimation(fadeOut);

                logoImageView.setVisibility(View.GONE);
                titleTextView.setVisibility(View.GONE);
                welcomeTextView.setVisibility(View.GONE);
                editText.setVisibility(View.GONE);
                submitButton.setVisibility(View.GONE);

                progressBar.setVisibility(View.VISIBLE);
            }
        });
        executor.execute(new Runnable() {

            @Override
            public void run() {

                String prompt = "Make a grocery list based on user input";

                if (initialPrompt != null && !initialPrompt.isEmpty()) {
                    prompt = initialPrompt;
                }

                GroceryListConstructor glc = new GroceryListConstructor(prompt, userInput, SplashActivity.this);
                try {
                    setQuestions(glc.followUpQuestions());
                    Log.d("FOLLOW UP QUESTIONS:", questions.toString());
                } catch(Exception e) {
                     e.printStackTrace();
                }

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Animation fadeOut = AnimationUtils.loadAnimation(SplashActivity.this, R.anim.fade_out);
                        progressBar.startAnimation(fadeOut);
                        progressBar.setVisibility(View.GONE);

                        displayNextQuestion(glc);
                    }
                });
            }
        });
    }

    private void animateEditTextHint(final EditText editText) {
        typingAnimation = new Runnable() {
            @Override
            public void run() {
                if (!isAnimationRunning) {
                    return;
                }
                String text = mealPlans[textIndex];
                if(text == null)
                    return;
                if (charIndex < text.length()) {
                    editText.setHint(text.substring(0, ++charIndex) + (cursorBlinkState ? "|" : ""));
                    handler.postDelayed(this, TYPING_INTERVAL);
                } else {
                    Log.d("string index:", String.valueOf(textIndex) +(String) editText.getHint());
                    if (++textIndex >= mealPlans.length)
                        textIndex = 0;
                    charIndex = 0;
                    handler.postDelayed(this, TEXT_PAUSE_DURATION);
                }
                if (cursorBlinkRunnable == null) {
                    startCursorBlink(editText);
                }
            }
        };
        handler.post(typingAnimation);
    }

    private void startCursorBlink(final EditText editText) {
        cursorBlinkRunnable = new Runnable() {
            @Override
            public void run() {
                String currentText = (String)editText.getHint();
                cursorBlinkState = !cursorBlinkState;
                String currentTextWithoutCursor = editText.getHint().toString().replace("|", "");
                editText.setHint(currentTextWithoutCursor + (cursorBlinkState ? "|" : ""));

                handler.postDelayed(this, CURSOR_BLINK_INTERVAL);
            }
        };
        handler.post(cursorBlinkRunnable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null)
        {
            handler.removeCallbacks(typingAnimation);
            handler.removeCallbacks(cursorBlinkRunnable);
        }

        if (executor != null && !executor.isShutdown()) {
            executor.shutdown();
        }

    }
    private void displayNextQuestion(GroceryListConstructor glc) {
        isAnimationRunning = false;
        editText.setHint("");
        if (currentQuestionIndex < questions.size()) {
            String currentQuestion = questions.get(currentQuestionIndex);

            textViewQuestion.setText(currentQuestion);
            editTextAnswer.setText("");
            fadeInUIComponents();

            buttonSubmit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String answer = editTextAnswer.getText().toString();

                    if (answer.equals("fake") || answer.isEmpty()) {
                        Toast.makeText(SplashActivity.this, "Please provide a proper response.", Toast.LENGTH_LONG).show();
                        editTextAnswer.requestFocus();
                        return;
                    }

                    Log.d("answer", answer);
                    processAnswer(answer);

                    currentQuestionIndex++;
                    displayNextQuestion(glc);
                }
            });
        } else {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    handler.post(new Runnable() {

                        @Override
                        public void run() {
                            Animation fadeIn = AnimationUtils.loadAnimation(SplashActivity.this, R.anim.fade_in);
                            Animation rotate = AnimationUtils.loadAnimation(SplashActivity.this, R.anim.rotate);

                            AnimationSet animationSet = new AnimationSet(true);
                            animationSet.addAnimation(rotate);
                            animationSet.addAnimation(fadeIn);

                            progressBar.startAnimation(animationSet);

                            Animation fadeOut = AnimationUtils.loadAnimation(SplashActivity.this, R.anim.fade_out);
                            welcomeTextView.startAnimation(fadeOut);
                            editText.startAnimation(fadeOut);
                            submitButton.startAnimation(fadeOut);

                            welcomeTextView.setVisibility(View.GONE);
                            editText.setVisibility(View.GONE);
                            submitButton.setVisibility(View.GONE);

                            progressBar.setVisibility(View.VISIBLE);
                        }
                    });
                    try (FileOutputStream fos = SplashActivity.this.openFileOutput("groceryItems.json", Context.MODE_PRIVATE)) {
                        fos.write(glc.generateGroceryList(questions, answers).toString().getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Intent mainIntent = new Intent(SplashActivity.this, MainActivity.class);
                            startActivity(mainIntent);
                            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                            finish();
                        }
                    });
                }

            });
        }
    }
    private void processAnswer(String answer) {
        answers.add(answer);
    }
    private void fadeInUIComponents() {

        Animation fadeInForText = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        Animation fadeInForEditText = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        Animation fadeInForButton = AnimationUtils.loadAnimation(this, R.anim.fade_in);

        textViewQuestion.setVisibility(View.VISIBLE);
        textViewQuestion.startAnimation(fadeInForText);

        editTextAnswer.setVisibility(View.VISIBLE);
        editTextAnswer.startAnimation(fadeInForEditText);

        buttonSubmit.setVisibility(View.VISIBLE);
        buttonSubmit.startAnimation(fadeInForButton);
    }

}


