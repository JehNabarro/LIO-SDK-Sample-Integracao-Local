package cielo.ordermanager.sdk.sample;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cielo.ordermanager.sdk.adapter.OrderRecyclerViewAdapter;
import cielo.ordermanager.sdk.listener.RecyclerItemClickListener;
import cielo.orders.domain.Credentials;
import cielo.orders.domain.Order;
import cielo.orders.domain.ResultOrders;
import cielo.sdk.order.OrderManager;
import cielo.sdk.order.ServiceBindListener;

public class CancellationOrderList extends AppCompatActivity {

    private final String TAG = "CANCELLATION_LIST";

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    private Order order;
    OrderManager orderManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cancellation_order_list);
        ButterKnife.bind(this);


    }

    @Override
    protected void onResume() {
        super.onResume();
        configSDK();
    }

    private void listOrders() {
        try {
            ResultOrders resultOrders = orderManager.retrieveOrders(20, 0);

            recyclerView.setLayoutManager(new LinearLayoutManager(CancellationOrderList.this));

            if (resultOrders != null) {
                final List<Order> orderList = resultOrders.getResults();

                recyclerView.setAdapter(
                        new OrderRecyclerViewAdapter(orderList));

                Log.i(TAG, "orders: " + orderList);
                for (Order or : orderList) {
                    Log.i("Order: ", or.getNumber() + " - " + or.getPrice());
                }

                recyclerView.addOnItemTouchListener(
                        new RecyclerItemClickListener(CancellationOrderList.this, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                order = orderList.get(position);

                                if (order.getPayments().size() > 0) {

                                    orderManager.unbind();

                                    Intent intent = new Intent(CancellationOrderList.this, CancelPaymentActivity.class);
                                    intent.putExtra("SELECTED_ORDER", order);
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(CancellationOrderList.this, "Não há pagamentos nessa ordem.", Toast.LENGTH_LONG).show();
                                }
                            }

                            @Override
                            public void onLongItemClick(View view, int position) {
                            }
                        })
                );
            }

        } catch (UnsupportedOperationException e) {
            Toast.makeText(CancellationOrderList.this, "FUNCAO NAO SUPORTADA NESSA VERSAO DA LIO", Toast.LENGTH_LONG).show();
        }
    }


    private void configSDK() {
        Credentials credentials = new Credentials("1234", "1234");
        orderManager = new OrderManager(credentials, this);
        orderManager.bind(this, new ServiceBindListener() {
            @Override
            public void onServiceBound() {
                listOrders();
            }

            @Override
            public void onServiceUnbound() {

            }
        });
    }
}
