package com.lianer.common.base;

import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lianer.common.R;

import java.util.List;

/**
 * 万能适配器
 * 使用demo
 *  quickAdapter = new QuickAdapter<String>(mnemonicData) {
@Override
public int getLayoutId(int viewType) {
return R.layout.item_mnemonic;
}

@Override
public void convert(VH holder, String data, int position) {
holder.setText(R.id.singel_mnemonic, data);
}

};
 * @param <T>
 */
public abstract class QuickAdapter<T> extends RecyclerView.Adapter<QuickAdapter.VH>{
    private List<T> mDatas;
    public QuickAdapter(List<T> datas){
        this.mDatas = datas;
    }

    public abstract int getLayoutId(int viewType);

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        return VH.get(parent,getLayoutId(viewType));
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {
        holder.setIsRecyclable(false);
        convert(holder, mDatas.get(position), position);
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public abstract void convert(VH holder, T data, int position);
    
    public static class VH extends RecyclerView.ViewHolder{
        private SparseArray<View> mViews;
        private View mConvertView;
    
        private VH(View v){
            super(v);
            mConvertView = v;
            mViews = new SparseArray<>();
        }
    
        public static VH get(ViewGroup parent, int layoutId){
            View convertView = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
            return new VH(convertView);
        }
    
        public <T extends View> T getView(int id){
            View v = mViews.get(id);
            if(v == null){
                v = mConvertView.findViewById(id);
                mViews.put(id, v);
            }
            return (T)v;
        }

        public void setText(int id, String value){
            TextView textView = getView(id);
            textView.setText(value);
        }

        public void setTextColor(int id, int textColor){
            TextView textView = getView(id);
            textView.setTextColor(textColor);
        }

        public void setImage(int id, int imgRes){
            ImageView imageView = getView(id);
            imageView.setImageResource(imgRes);
            imageView.setVisibility(View.VISIBLE);
        }

        public void setTextBackground(int id, int backgroudRes){
            TextView textView = getView(id);
            textView.setBackgroundResource(backgroudRes);
        }

        public void setItemBackgroundColor(int id, int backgroudRes){
            View view = getView(id);
            view.setBackgroundColor(backgroudRes);
        }

    }


}