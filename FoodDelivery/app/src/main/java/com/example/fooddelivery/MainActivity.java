package com.example.fooddelivery;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

class CardAdapter extends ArrayAdapter<CardData> {

    public CardAdapter(Context context, List<CardData> cardList) {
        super(context, 0, cardList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.card_view, parent, false);
        }

        CardData cardData = getItem(position);
        TextView foodName = convertView.findViewById(R.id.foodTitle);
        foodName.setText(cardData.FoodName);

        TextView foodPrice = convertView.findViewById(R.id.foodPrice);
        foodPrice.setText(cardData.Price);

        ImageView imgView = convertView.findViewById(R.id.imageView);
        switch (cardData.ImgSource)
        {
            case "bokho":
                imgView.setBackgroundResource(R.drawable.bokho);
                break;
            case "bunbo":
                imgView.setBackgroundResource(R.drawable.bunbo);
                break;
            case "com":
                imgView.setBackgroundResource(R.drawable.com);
                break;
            case "ga":
                imgView.setBackgroundResource(R.drawable.ga);
                break;
            case "suon":
                imgView.setBackgroundResource(R.drawable.suon);
                break;
        }
        return convertView;
    }
}

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        List<CardData> cardList = generateCardData();
        CardAdapter cardAdapter = new CardAdapter(this, cardList);


        ListView myList = findViewById(R.id.foodList);
        myList.setAdapter(cardAdapter);

    }
    private List<CardData> generateCardData() {
        List<CardData> cardList = new ArrayList<>();
        cardList.add(new CardData("Gà Nướng", "30.000 VNĐ", "ga"));
        cardList.add(new CardData("Sườn Nướng", "20.000 VNĐ", "suon"));
        cardList.add(new CardData("Cơm Tấm", "25.000 VNĐ", "com"));
        cardList.add(new CardData("Bún Bò", "35.000 VNĐ", "bunbo"));
        cardList.add(new CardData("Bò Kho", "25.000 VNĐ", "bokho"));

        return cardList;
    }
}