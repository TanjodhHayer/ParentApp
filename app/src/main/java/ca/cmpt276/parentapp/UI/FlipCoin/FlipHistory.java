package ca.cmpt276.parentapp.UI.FlipCoin;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import ca.cmpt276.parentapp.R;
import ca.cmpt276.parentapp.model.Child.Child;
import ca.cmpt276.parentapp.model.Coin.Coin;
import ca.cmpt276.parentapp.model.Coin.CoinManager;

public class FlipHistory extends AppCompatActivity {
    private TextView NoHistoryTextView;
    private CoinManager coinManager;

    public static Intent makeIntent(Context context) {
        return new Intent(context, FlipHistory.class);
    }

    private void initItems() {
        coinManager = CoinManager.getInstance();
        NoHistoryTextView = findViewById(R.id.NoHistoryTextView);
    }

    @Override
    protected void onStart() {
        initItems();
        if (coinManager.getCoinHistory().isEmpty()) {
            NoHistoryTextView.setVisibility(View.VISIBLE);
        } else {
            NoHistoryTextView.setVisibility(View.INVISIBLE);
        }
        listCoins();
        super.onStart();
    }

    private void listCoins() {
        ArrayAdapter<Coin> adapter = new adapter();
        ListView list = findViewById(R.id.FlipHistoryListView);
        list.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flip_history);
        setTitle("Flip Coin History");

        initItems();
        listCoins();
    }

    public class adapter extends ArrayAdapter<Coin> {
        public adapter() {
            super(FlipHistory.this, R.layout.flip_history_list_view, coinManager.getCoinHistory());
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View itemView = convertView;
            if (itemView == null)
                itemView = getLayoutInflater().inflate(R.layout.flip_history_list_view, parent, false);

            Coin currCoin = coinManager.getCoinHistory().get(position);
            TextView txt = itemView.findViewById(R.id.CoinTextView);
            txt.setText(currCoin.toString());

            ImageView winOrNotImage = itemView.findViewById(R.id.CoinImageView);
            ImageView childImage = itemView.findViewById(R.id.ChildImageHistory);

            Child currKid = currCoin.getChild();
            if (currKid != null) {
                if (currKid.getName().equals(currCoin.getChildName()) && currKid.getImg() != null && currKid.getImg() != "") {
                    try {
                        File f = new File(currKid.getImg(), currKid.getImgName());
                        Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
                        childImage.setImageBitmap(b);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                } else {
                    childImage.setImageResource(R.drawable.childimg);
                }
            }


            if (currCoin.isWinOrNot()) {
                winOrNotImage.setImageResource(R.drawable.win);
            } else {
                winOrNotImage.setImageResource(R.drawable.lose);
            }

            return itemView;
        }
    }


}