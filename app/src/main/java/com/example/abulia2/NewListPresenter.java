package com.example.abulia2;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Random;

//This class is meant to create text fields and buttons for the user to enter potential choices into
public class NewListPresenter implements MVPComponents.NewListPresenterContract {

    private NewListActivity viewNewList;
    private MVPComponents.NewListModel modelNewList;
    private ArrayList<EditText> optionList = new ArrayList<>();
    //The number of options to choose from will never be less than 2
    private int optionCount = 0;
    private int optionID = 0;
    private String listName;

    public NewListActivity getNewListView(){
        return viewNewList;
    }

    public NewListPresenter(NewListActivity view){
        viewNewList = view;
        modelNewList = new NewListModel(this);
        addOptionField();
        addOptionField();
    }

     public void choose(){
         removeEmptyOptions();
         Random r = new Random();
         String chosenOption;
         do {
             int textId = r.nextInt(optionCount);
             Log.d(getClass().getName(), "chooseOption() textView id = " + textId + " editTextCount = " + optionCount);
             chosenOption = optionList.get(textId).getText().toString();
         }while(chosenOption.isEmpty());
            showChoice(chosenOption);
    }

    public void saveListAs(){
        final AlertDialog.Builder alert = new AlertDialog.Builder(viewNewList);
        final EditText input = new EditText(viewNewList);

        alert.setTitle("Save List As...");
        alert.setMessage("Enter new list name:");
        alert.setView(input);
        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                if(!input.getText().toString().isEmpty()) {
                    listName = input.getText().toString();
                    Log.d(getClass().getName(), "listName = " + listName);
                    removeEmptyOptions();
                    modelNewList.saveListAsTable(listName, optionList);
                }else{
                    alert("Enter valid name");
                }
            }
        });
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Cancel
            }
        });
        alert.show();
    }

    private void removeEmptyOptions(){
        Log.d(getClass().getName(), "removeEmptyOptions() printing list before");
        printList();
        for(int i = 0; i < optionList.size(); i++){
            Log.d(getClass().getName(), "allOptionsFilled() optionList.toString = " + optionList.get(i));
            if(TextUtils.isEmpty(optionList.get(i).getText().toString()) ){
                EditText removedET = optionList.get(i);
                ((ViewGroup) removedET.getParent()).removeAllViews();
                optionList.remove(i);
                optionCount--;
                i--;
            }
        }
    }

    private void printList(){
        for(int i = 0; i < optionList.size(); i++){
            Log.d(getClass().getName(), "Printing optionList: " + optionList.get(i));
        }
    }

    public void addOptionField(){
        if(optionCount < 10) {
            EditText et = new EditText(viewNewList);

            LinearLayout parentContainer = viewNewList.findViewById(R.id.optionLayout);
            LinearLayout optionContainer = new LinearLayout(viewNewList);

            et.setHint("Option");
            et.setLayoutParams(new ViewGroup.LayoutParams(600, 100));
            optionList.add(et);
            optionContainer.setId(optionID);
            optionID++;
            optionCount++;
            optionContainer.setOrientation(LinearLayout.HORIZONTAL);
            optionContainer.setLayoutParams
                    (new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.MATCH_PARENT));

            optionContainer.addView(et);
            optionContainer.addView(createCancelButton(parentContainer, optionContainer, et));
            parentContainer.addView(optionContainer);
            printList();
        }else{
            alert("Maximum Options Reached");
        }
    }

    private ImageButton createCancelButton(final LinearLayout parent,
                                           final LinearLayout container, final EditText option ){
        final ImageButton cancelButton = new ImageButton(viewNewList);
        cancelButton.setId(optionID);
        cancelButton.setImageDrawable(viewNewList.getApplicationContext()
                .getResources().getDrawable(R.drawable.ic_close));
        cancelButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                //disallow less than two options
                if(optionCount > 2) {
                    Log.d(getClass().getName(), "createCancelButton() Deleting option "
                            + option.getText());
                    optionList.remove(option);

                    for (int i = 0; i < optionList.size(); i++) {
                        Log.d(getClass().getName(), "createCancelButton() List[" + i + "] = "
                                + optionList.get(i));
                    }
                    parent.removeView(container);
                    optionCount--;
                }
            }
        });
        return cancelButton;
    }
    /**
     * Alerts the user if something is awry
     * @param alertMsg what the alert should say
     */
    private void alert(String alertMsg){
        AlertDialog.Builder aDialog = new AlertDialog.Builder(viewNewList);
        TextView alert = new TextView(viewNewList);
        alert.setTextSize(24);
        alert.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        alert.setText(alertMsg);
        alert.setGravity(Gravity.CENTER_HORIZONTAL);
        aDialog.setView(alert);
        aDialog.show();
    }

    private void showChoice(String choice){
        Log.d(getClass().getName(), "Choice is " + choice);
        final PopupWindow choicePopUp = new PopupWindow(viewNewList);
        final LinearLayout ll = new LinearLayout(viewNewList);
        TextView choiceTV = new TextView(viewNewList);

        choiceTV.setText(choice);
        choiceTV.setTextSize(35);
        choiceTV.setGravity(Gravity.CENTER_HORIZONTAL);
        ll.setOrientation(LinearLayout.VERTICAL);
        ll.addView(choiceTV);
        choicePopUp.setContentView(ll);
        choicePopUp.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        choicePopUp.setOutsideTouchable(true);
        choicePopUp.setFocusable(true);
        choicePopUp.showAtLocation(ll, Gravity.CENTER_HORIZONTAL, 10, 10);
    }
}
