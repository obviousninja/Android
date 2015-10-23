package us.in_tune.in_tunex3;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by Randy on 10/18/2015.
 */
public class SummaryCardViewAdapter extends RecyclerView.Adapter<SummaryCardViewAdapter.ViewHolder> {

    ArrayList<NameAndPrice> grandlist;

    public SummaryCardViewAdapter(ArrayList<NameAndPrice> items){
        grandlist = items;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public android.view.View View;
        public ViewHolder(View v) {
            super(v);
            View = v;
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        //View curView = LayoutInflater.from(parent.getContext())
        //       .inflate(R.layout.schedule_card_item, parent, false);

        View curView =  LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_summary, parent, false);

        ViewHolder vh = new ViewHolder(curView);


        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        TextView serviceName = (TextView) holder.View.findViewById(R.id.item_summary_service_name);
        TextView serviceQuantity = (TextView) holder.View.findViewById(R.id.item_summary_service_quantity);
        TextView servicePrice = (TextView) holder.View.findViewById(R.id.item_summary_service_price);
        TextView serviceOption = (TextView) holder.View.findViewById(R.id.item_summary_service_option);

        String summary_card_adapter_service_name = grandlist.get(position).getServiceName();
        String summary_card_adapter_service_quantity = grandlist.get(position).getQuantity();
        String summary_card_adapter_service_base_price = grandlist.get(position).getBasePrice();
        String summary_card_adapter_service_extra_price = grandlist.get(position).getExtraPrice();
        Integer summary_card_adapter_sumPrice = Integer.parseInt(summary_card_adapter_service_base_price)+ Integer.parseInt(summary_card_adapter_service_extra_price);
        String summary_card_adapter_service_option = grandlist.get(position).getServiceOption();

        serviceName.setText(summary_card_adapter_service_name);
        serviceQuantity.setText(summary_card_adapter_service_quantity);
        servicePrice.setText(summary_card_adapter_sumPrice.toString());
        serviceOption.setText(summary_card_adapter_service_option);

    }

    @Override
    public int getItemCount() {
        return grandlist.size();
    }
}
