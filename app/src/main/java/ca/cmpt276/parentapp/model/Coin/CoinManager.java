package ca.cmpt276.parentapp.model.Coin;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Iterator;

public class CoinManager implements Iterable<Coin> {
    private static final CoinManager instance = new CoinManager();
    private ArrayList<Coin> coinHistoryArrayList = new ArrayList<>();

    private CoinManager() {
    }

    public static CoinManager getInstance() {
        return instance;
    }

    public ArrayList<Coin> getCoinHistory() {
        if (coinHistoryArrayList == null) coinHistoryArrayList = new ArrayList<>();
        return coinHistoryArrayList;
    }

    public void setCoinHistoryArrayList(ArrayList<Coin> coinHistoryArrayList) {
        this.coinHistoryArrayList = coinHistoryArrayList;
    }

    public void addCoinHistory(Coin coinHistory) {
        if (coinHistoryArrayList == null) coinHistoryArrayList = new ArrayList<>();
        coinHistoryArrayList.add(coinHistory);
    }

    public void removeCoinHistory(int index) {
        coinHistoryArrayList.remove(index);
    }

    @NonNull
    @Override
    public Iterator<Coin> iterator() {
        return coinHistoryArrayList.iterator();
    }
}
