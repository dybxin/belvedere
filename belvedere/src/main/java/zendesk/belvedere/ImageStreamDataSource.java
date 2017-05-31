package zendesk.belvedere;

import android.net.Uri;
import android.support.v7.util.DiffUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class ImageStreamDataSource {

    private List<ImageStreamItems.Item> staticItems;
    private List<ImageStreamItems.Item> imageStream;

    private List<ImageStreamItems.Item> list;

    ImageStreamDataSource() {
        staticItems = new ArrayList<>();
        imageStream = new ArrayList<>();
        list = new ArrayList<>();
    }

    ImageStreamItems.Item getItemForPos(int pos) {
        return list.get(pos);
    }

    int getItemCount() {
        return list.size();
    }

    DiffUtil.DiffResult initializeWithImages(List<ImageStreamItems.Item> imageStream) {
        return updateDataSet(staticItems, imageStream);
    }

    DiffUtil.DiffResult setItemsSelected(List<Uri> uris) {
        final ArrayList<ImageStreamItems.Item> streamItemImages = new ArrayList<>(imageStream);

        for(ImageStreamItems.Item item : streamItemImages) {
            final boolean selected = uris.contains(item.getMediaResult().getOriginalUri());
            item.setSelected(selected);
        }

        return updateDataSet(staticItems, streamItemImages);
    }

    DiffUtil.DiffResult addStaticItem(ImageStreamItems.Item staticItem) {
        return updateDataSet(Collections.singletonList(staticItem), imageStream);
    }

    private synchronized DiffUtil.DiffResult updateDataSet(List<ImageStreamItems.Item> newStaticItems,
                                                           List<ImageStreamItems.Item> newImageStream) {
        List<ImageStreamItems.Item> newList = new ArrayList<>(newStaticItems.size() + newImageStream.size());
        newList.addAll(newStaticItems);
        newList.addAll(newImageStream);

        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffHelper(list, newList));

        staticItems = newStaticItems;
        imageStream = newImageStream;
        list = newList;

        return diffResult;
    }

    private static class DiffHelper extends DiffUtil.Callback {

        private final List<ImageStreamItems.Item> oldItems;
        private final List<ImageStreamItems.Item> newItems;

        private DiffHelper(List<ImageStreamItems.Item> oldItems, List<ImageStreamItems.Item> newItems) {
            this.oldItems = oldItems;
            this.newItems = newItems;
        }

        @Override
        public int getOldListSize() {
            return oldItems.size();
        }

        @Override
        public int getNewListSize() {
            return newItems.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            return oldItems.get(oldItemPosition).getId() == newItems.get(newItemPosition).getId();
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            return oldItems.get(oldItemPosition).isSelected() == newItems.get(newItemPosition).isSelected();
        }
    }
}