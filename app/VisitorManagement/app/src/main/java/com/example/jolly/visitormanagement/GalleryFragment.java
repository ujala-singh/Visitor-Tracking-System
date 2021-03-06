package com.example.jolly.visitormanagement;



import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.jolly.visitormanagement.models.GalleryModel;

import java.util.ArrayList;



/**
 * A simple {@link Fragment} subclass.
 */
public class GalleryFragment extends Fragment {

    View mview;
    private ArrayList<GalleryModel> itemList;
    private RecyclerView containerRV;


    public GalleryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        mview = inflater.inflate(R.layout.fragment_gallery, container, false);
        containerRV = (RecyclerView) mview.findViewById(R.id.containerRV);
        generateData();

        //this function tells the system that it will be a linear list of cards in vertical orientation.
        containerRV.setLayoutManager(new LinearLayoutManager(getActivity()));
        MyAdapter adapter = new MyAdapter();
        containerRV.setAdapter(adapter);
        return mview;
    }

    private void generateData() {
        itemList = new ArrayList();
//        al = new ArrayList<>();
        //two versions: v1
//        MyModel model1 = new MyModel("First title", "Content for first title", R.drawable.bulb);
//        itemList.add(0, model1);
        //v2: easier and memory saver
        itemList.add(new GalleryModel("Computer Center 3\nThe main academic \nbuilding of IIITA.", R.drawable.cc3));

        itemList.add(new GalleryModel("Visitor Hostel 1\nThis is the first \nvisitor hostel.", R.drawable.vh1));

        itemList.add(new GalleryModel("Visitor Hostel 2\nThis is the second \nvisitor hostel", R.drawable.vh2));

        itemList.add(new GalleryModel("Administrative Block\nThe administrative \nbuilding of IIITA is the \nheart of the institute.", R.drawable.admin));
//        itemList.add(new GalleryModel("Fifth title", "Content for fifth title. Some text to be added.", R.drawable.linux_icon));
//        itemList.add(new GalleryModel("Sixth title", "Content for sixth title. Some text to be added.", R.drawable.tomato));
    }

    public int getSize() {
        return itemList.size();
    }


    class MyAdapter extends RecyclerView.Adapter<GalleryHolder> {

        /**
         * Called when RecyclerView needs a new {@link GalleryHolder} of the given type to represent
         * an item.
         * <p>
         * This new ViewHolder should be constructed with a new View that can represent the items
         * of the given type. You can either create a new View manually or inflate it from an XML
         * layout file.
         * <p>
         * The new ViewHolder will be used to display items of the adapter using
         * {@link #onBindViewHolder(GalleryHolder, int)}. Since it will be re-used to display
         * different items in the data set, it is a good idea to cache references to sub views of
         * the View to avoid unnecessary {@link View#findViewById(int)} calls.
         *
         * @param parent   The ViewGroup into which the new View will be added after it is bound to
         *                 an adapter position.
         * @param viewType The view type of the new View.
         * @return A new ViewHolder that holds a View of the given view type.
         * @see #getItemViewType(int)
         * @see #onBindViewHolder(GalleryHolder, int)
         */
        @Override
        public GalleryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//            return null;
            // an object of layout file is created with ref v
            View itemView = LayoutInflater.from(getActivity()).inflate(R.layout.gallery_card_items, parent, false);
            return new GalleryHolder(itemView);
        }


        /**
         * Called by RecyclerView to display the data at the specified position. This method should
         * update the contents of the {@link GalleryHolder#itemView} to reflect the item at the given
         * position.
         * <p>
         * Note that unlike {@link ListView}, RecyclerView will not call this method
         * again if the position of the item changes in the data set unless the item itself is
         * invalidated or the new position cannot be determined. For this reason, you should only
         * use the <code>position</code> parameter while acquiring the related data item inside
         * this method and should not keep a copy of it. If you need the position of an item later
         * on (e.g. in a click listener), use {@link GalleryHolder#getAdapterPosition()} which will
         * have the updated adapter position.
         * <p>
         * Override {@link #onBindViewHolder(GalleryHolder, int)} instead if Adapter can
         * handle efficient partial bind.
         *
         * @param holder   The ViewHolder which should be updated to represent the contents of the
         *                 item at the given position in the data set.
         * @param position The position of the item within the adapter's data set.
         */
        @Override
        /**
         * data binding
         *     /    \
         * layout  data  mapping
         */
        public void onBindViewHolder(GalleryHolder holder, int position) {
            GalleryModel item = itemList.get(position);
//            holder.tvPerm.setText(item.getTitle());
            String str = item.getContent();
            holder.tvContent.setText(str);
            holder.tvContent.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_FLAG_MULTI_LINE);
//            String str1 = item.getOffice();
//            holder.tvOffice.setText(str1);
            /*if (str.compareToIgnoreCase("Granted") == 0) {
                holder.tvContent.setTextColor(Color.GREEN);
            } else {
                holder.tvContent.setTextColor(Color.RED);
            }*/

//            holder.containerCL.setBackgroundResource(item.getImg());
            ImageView view = holder.containerCL.findViewById(R.id.imFac);
            view.setImageResource(item.getImg());
//            view.setImageURI(item.getImg());
        }

        /**
         * Returns the total number of items in the data set held by the adapter.
         *
         * @return The total number of items in this adapter.
         */
        @Override
        public int getItemCount() {
            return itemList.size();
        }
    }


}
