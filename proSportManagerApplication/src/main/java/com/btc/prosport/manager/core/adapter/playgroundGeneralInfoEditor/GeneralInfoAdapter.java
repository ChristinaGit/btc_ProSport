package com.btc.prosport.manager.core.adapter.playgroundGeneralInfoEditor;

import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.val;

import com.btc.common.contract.Contracts;
import com.btc.common.event.Events;
import com.btc.common.event.generic.ManagedEvent;
import com.btc.common.event.notice.ManagedNoticeEvent;
import com.btc.common.event.notice.NoticeEvent;
import com.btc.common.event.notice.NoticeEventHandler;
import com.btc.common.extension.delegate.loading.LoadingViewDelegate;
import com.btc.common.extension.delegate.loading.ProgressVisibilityHandler;
import com.btc.common.extension.view.recyclerView.adapter.RecyclerViewAdapter;
import com.btc.common.extension.view.recyclerView.decoration.DividerItemDecoration;
import com.btc.common.extension.view.recyclerView.viewHolder.ExtendedRecyclerViewHolder;
import com.btc.common.utility.ThemeUtils;
import com.btc.common.utility.listener.SimpleTextWatcher;
import com.btc.prosport.api.model.City;
import com.btc.prosport.api.model.SubwayStation;
import com.btc.prosport.manager.R;
import com.btc.prosport.manager.core.adapter.playgroundGeneralInfoEditor.item.CityItem;
import com.btc.prosport.manager.core.adapter.playgroundGeneralInfoEditor.item.NameItem;
import com.btc.prosport.manager.core.adapter.playgroundGeneralInfoEditor.item.PhoneNumberItem;
import com.btc.prosport.manager.core.adapter.playgroundGeneralInfoEditor.item.PhotosItem;
import com.btc.prosport.manager.core.adapter.playgroundGeneralInfoEditor.item.PlaceItem;
import com.btc.prosport.manager.core.adapter.playgroundGeneralInfoEditor.item.SubwayItem;
import com.btc.prosport.manager.core.adapter.playgroundGeneralInfoEditor.item.TitleItem;
import com.btc.prosport.manager.core.adapter.playgroundGeneralInfoEditor.viewHolder.CityViewHolder;
import com.btc.prosport.manager.core.adapter.playgroundGeneralInfoEditor.viewHolder.NameViewHolder;
import com.btc.prosport.manager.core.adapter.playgroundGeneralInfoEditor.viewHolder
    .PhoneNumberViewHolder;
import com.btc.prosport.manager.core.adapter.playgroundGeneralInfoEditor.viewHolder
    .PhotosViewHolder;
import com.btc.prosport.manager.core.adapter.playgroundGeneralInfoEditor.viewHolder.PlaceViewHolder;
import com.btc.prosport.manager.core.adapter.playgroundGeneralInfoEditor.viewHolder
    .SubwayViewHolder;
import com.btc.prosport.manager.core.adapter.playgroundGeneralInfoEditor.viewHolder.TitleViewHolder;
import com.btc.prosport.manager.core.adapter.playgroundPhotoEditor.PhotosAdapter;
import com.btc.prosport.manager.core.adapter.playgroundPhotoEditor.item.PhotoItem;
import com.btc.prosport.manager.core.eventArgs.PlacePickerEventArgs;
import com.btc.prosport.manager.core.eventArgs.UpdateSubwaysEventArgs;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.text.NumberFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

// TODO: Need to save focus, and cursor position.
// TODO: Use divider as item.
// TODO: Validate fields.
@Accessors(prefix = "_")
public class GeneralInfoAdapter extends RecyclerViewAdapter<ExtendedRecyclerViewHolder> {
    public static final int VIEW_TYPE_NAME;

    public static final int VIEW_TYPE_PLACE;

    public static final int VIEW_TYPE_TITLE;

    public static final int VIEW_TYPE_PHONE_NUMBER;

    public static final int VIEW_TYPE_CITY;

    public static final int VIEW_TYPE_SUBWAY;

    public static final int VIEW_TYPE_PHOTOS;

    public static final int[] VIEW_TYPE_ORDER;

    static {
        int viewTypeIndexer = 0;

        VIEW_TYPE_NAME = ++viewTypeIndexer;
        VIEW_TYPE_PLACE = ++viewTypeIndexer;
        VIEW_TYPE_PHONE_NUMBER = ++viewTypeIndexer;
        VIEW_TYPE_CITY = ++viewTypeIndexer;
        VIEW_TYPE_SUBWAY = ++viewTypeIndexer;
        VIEW_TYPE_PHOTOS = ++viewTypeIndexer;
        VIEW_TYPE_TITLE = ++viewTypeIndexer;

        VIEW_TYPE_ORDER = new int[]{
            VIEW_TYPE_NAME,
            VIEW_TYPE_PLACE,
            VIEW_TYPE_TITLE,
            VIEW_TYPE_PHONE_NUMBER,
            VIEW_TYPE_TITLE,
            VIEW_TYPE_CITY,
            VIEW_TYPE_TITLE,
            VIEW_TYPE_SUBWAY,
            VIEW_TYPE_TITLE,
            VIEW_TYPE_PHOTOS};
    }

    public void addPhoneItem(
        @NonNull final PhoneNumberItem phoneNumberItem, final boolean notifyAdapter) {
        Contracts.requireNonNull(phoneNumberItem, "phoneNumberItem == null");

        val phoneNumberItems = getPhoneNumberItems();
        phoneNumberItems.append(getNextId(phoneNumberItems), phoneNumberItem);

        if (notifyAdapter) {
            final int position =
                getItemsPositionByType(phoneNumberItem.getItemType()) + phoneNumberItems.size() - 1;
            if (phoneNumberItems.size() > 1) {
                notifyItemChanged(position - 1);
            }
            notifyItemInserted(position);
        }
    }

    // TODO: Need to think. Adds photo with animation.
    public void addPhotoItems(
        @NonNull final List<PhotoItem> photoItems, final boolean notifyAdapter) {
        Contracts.requireNonNull(photoItems, "photoItems == null");

        if (_photosAdapter != null) {
            _photosAdapter.addPhotoItems(photoItems, notifyAdapter);
        }
    }

    public void addSubwayItems(
        @NonNull final List<SubwayItem> addedSubwayItems, final boolean notifyAdapter) {
        Contracts.requireNonNull(addedSubwayItems, "addedSubwayItems == null");

        val subwayItems = getSubwayItems();

        int countInserted = addedSubwayItems.size();
        final int positionStart = getItemsPositionByType(VIEW_TYPE_SUBWAY) + subwayItems.size();

        if (subwayItems.size() == 0) {
            countInserted++;
        }

        for (final val subwayItem : addedSubwayItems) {
            subwayItems.append(getNextId(subwayItems), subwayItem);
        }

        if (notifyAdapter) {
            notifyItemRangeInserted(positionStart, countInserted);
            if (subwayItems.size() > 1) {
                notifyItemChanged(positionStart - 1);
            }
        }
    }

    public void clearPhoneItems(final boolean notifyAdapter) {
        final int itemsPositionByType = getItemsPositionByType(VIEW_TYPE_PHONE_NUMBER);
        final int size = _phoneNumberItems.size();
        _phoneNumberItems.clear();

        if (notifyAdapter) {
            if (size > 1) {
                notifyItemRangeRemoved(itemsPositionByType + 2, size - 1);
            }
            notifyItemChanged(itemsPositionByType + 1);
        }
    }

    public void clearSubways() {
        val subwayItems = getSubwayItems();
        if (subwayItems.size() > 0) {
            final int removedPosition = getItemsPositionByType(VIEW_TYPE_SUBWAY) - 1;
            final int removedSize = subwayItems.size() + 1;
            subwayItems.clear();
            notifyItemRangeRemoved(removedPosition, removedSize);
        }
    }

    @NonNull
    public GeneralInfoItem getItem(final int position, final int viewType) {
        final GeneralInfoItem item;

        if (VIEW_TYPE_NAME == viewType) {
            item = getNameItem();
        } else if (VIEW_TYPE_PLACE == viewType) {
            item = getPlaceItem();
        } else if (VIEW_TYPE_PHONE_NUMBER == viewType) {
            item = getPhoneNumberItems().valueAt(position);
        } else if (VIEW_TYPE_CITY == viewType) {
            item = getCityItem();
        } else if (VIEW_TYPE_SUBWAY == viewType) {
            item = getSubwayItems().valueAt(position);
        } else if (VIEW_TYPE_PHOTOS == viewType) {
            item = getPhotosItem();
        } else {
            throw new IllegalArgumentException("Unknown view type: " + viewType);
        }

        return item;
    }

    @NonNull
    public GeneralInfoItem getItem(final int position) {
        GeneralInfoItem item = null;
        int currentItemCount = 0;
        final int length = VIEW_TYPE_ORDER.length;
        for (int i = 0; i < length; i++) {
            final int viewType = VIEW_TYPE_ORDER[i];
            final int typedItemCount;
            final boolean isTitle = viewType == VIEW_TYPE_TITLE;
            if (isTitle) {
                if (i + 1 != length) {
                    typedItemCount = getTitleCount(VIEW_TYPE_ORDER[i + 1]);
                } else {
                    typedItemCount = 0;
                }
            } else {
                typedItemCount = getItemCount(viewType);
            }
            currentItemCount += typedItemCount;
            if (position < currentItemCount) {
                if (isTitle) {
                    if (i + 1 != length) {
                        item = getTitle(VIEW_TYPE_ORDER[i + 1]);
                    }
                } else {
                    item = getItem(position - currentItemCount + typedItemCount, viewType);
                }
                break;
            }
        }

        if (item == null) {
            throw new IllegalArgumentException("Item not found. Position: " + position);
        }

        return item;
    }

    @CallSuper
    public int getItemCount(final int viewType) {
        final int itemCount;

        if (VIEW_TYPE_NAME == viewType || VIEW_TYPE_PLACE == viewType ||
            VIEW_TYPE_CITY == viewType || VIEW_TYPE_PHOTOS == viewType) {
            itemCount = 1;
        } else if (VIEW_TYPE_PHONE_NUMBER == viewType) {
            val phoneNumberItems = getPhoneNumberItems();
            final int size = phoneNumberItems.size();
            if (size == 0) {
                phoneNumberItems.append(getNextId(phoneNumberItems), new PhoneNumberItem());
            }
            itemCount = size;
        } else if (VIEW_TYPE_SUBWAY == viewType) {
            itemCount = getSubwayItems().size();
        } else {
            throw new IllegalArgumentException("Unknown view type: " + viewType);
        }

        return itemCount;
    }

    @CallSuper
    public int getItemsPositionByType(final int viewType) {
        int itemsPosition = 0;
        final int length = VIEW_TYPE_ORDER.length;
        for (int i = 0; i < length; i++) {
            final int type = VIEW_TYPE_ORDER[i];
            if (type == viewType) {
                break;
            }
            final int typedItemCount;
            if (type == VIEW_TYPE_TITLE) {
                if (i + 1 != length) {
                    typedItemCount = getTitleCount(VIEW_TYPE_ORDER[i + 1]);
                } else {
                    typedItemCount = 0;
                }
            } else {
                typedItemCount = getItemCount(type);
            }
            itemsPosition += typedItemCount;
        }
        return itemsPosition;
    }

    public NoticeEvent getPickPhotoEvent() {
        return _pickPhotoEvent;
    }

    @CallSuper
    public int getTitleCount(final int nextViewType) {
        final int titleCount;

        if (nextViewType == VIEW_TYPE_CITY || nextViewType == VIEW_TYPE_PHONE_NUMBER ||
            nextViewType == VIEW_TYPE_PHOTOS) {
            titleCount = 1;
        } else if (nextViewType == VIEW_TYPE_SUBWAY) {
            titleCount = getSubwayItems().size() == 0 ? 0 : 1;
        } else {
            throw new IllegalArgumentException("Unknown next view type: " + nextViewType);
        }

        return titleCount;
    }

    @Override
    public ExtendedRecyclerViewHolder onCreateViewHolder(
        final ViewGroup parent, final int viewType) {
        final ExtendedRecyclerViewHolder viewHolder;

        if (VIEW_TYPE_NAME == viewType) {
            viewHolder = onCreateNameViewHolder(parent);
        } else if (VIEW_TYPE_PLACE == viewType) {
            viewHolder = onCreatePlaceViewHolder(parent);
        } else if (VIEW_TYPE_PHONE_NUMBER == viewType) {
            viewHolder = onCreatePhoneNumberViewHolder(parent);
        } else if (VIEW_TYPE_CITY == viewType) {
            viewHolder = onCreateCityViewHolder(parent);
        } else if (VIEW_TYPE_SUBWAY == viewType) {
            viewHolder = onCreateSubwaysViewHolder(parent);
        } else if (VIEW_TYPE_PHOTOS == viewType) {
            viewHolder = onCreatePhotosViewHolder(parent);
        } else if (VIEW_TYPE_TITLE == viewType) {
            viewHolder = onCreateTitleViewHolder(parent);
        } else {
            throw new IllegalArgumentException("Unknown view type: " + viewType);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(
        @NonNull final ExtendedRecyclerViewHolder holder, final int position) {

        final val generalInfoItem = getItem(position);

        final int itemType = generalInfoItem.getItemType();
        if (VIEW_TYPE_NAME == itemType) {
            displayDivider(holder, true);
            onBindNameViewHolder((NameViewHolder) holder, (NameItem) generalInfoItem);
        } else if (VIEW_TYPE_PLACE == itemType) {
            displayDivider(holder, true);
            onBindPlaceViewHolder((PlaceViewHolder) holder, (PlaceItem) generalInfoItem);
        } else if (VIEW_TYPE_PHONE_NUMBER == itemType) {
            if (position ==
                getItemsPositionByType(VIEW_TYPE_PHONE_NUMBER) + getPhoneNumberItems().size() - 1) {
                displayDivider(holder, true);
            } else {
                displayDivider(holder, false);
            }
            onBindPhoneNumberViewHolder((PhoneNumberViewHolder) holder,
                                        (PhoneNumberItem) generalInfoItem);
        } else if (VIEW_TYPE_CITY == itemType) {
            displayDivider(holder, true);
            onBindCityViewHolder((CityViewHolder) holder, (CityItem) generalInfoItem);
        } else if (VIEW_TYPE_SUBWAY == itemType) {
            displayDivider(holder, true);
            final int size = getSubwayItems().size();
            if (size != 0 && position == getItemsPositionByType(VIEW_TYPE_SUBWAY) + size - 1) {
                displayDivider(holder, true);
            } else {
                displayDivider(holder, false);
            }
            onBindSubwaysViewHolder((SubwayViewHolder) holder, (SubwayItem) generalInfoItem);
        } else if (VIEW_TYPE_PHOTOS == itemType) {
            displayDivider(holder, false);
            onBindPhotosViewHolder((PhotosViewHolder) holder, (PhotosItem) generalInfoItem);
        } else if (VIEW_TYPE_TITLE == itemType) {
            displayDivider(holder, false);
            onBindTitleViewHolder((TitleViewHolder) holder, (TitleItem) generalInfoItem);
        }
    }

    @Override
    public int getItemViewType(final int position) {
        return getItem(position).getItemType();
    }

    @Override
    public long getItemId(final int position) {
        if (getItem(position).getItemType() == VIEW_TYPE_PHONE_NUMBER) {
            return getPhoneNumberItems().keyAt(
                position - getItemsPositionByType(VIEW_TYPE_PHONE_NUMBER));
        } else if (getItem(position).getItemType() == VIEW_TYPE_SUBWAY) {
            return getSubwayItems().keyAt(position - getItemsPositionByType(VIEW_TYPE_SUBWAY));
        }
        return super.getItemId(position);
    }

    @CallSuper
    @Override
    public int getItemCount() {
        int itemCount = 0;

        final int length = VIEW_TYPE_ORDER.length;
        for (int i = 0; i < length; i++) {
            final int viewType = VIEW_TYPE_ORDER[i];
            if (viewType == VIEW_TYPE_TITLE) {
                if (i + 1 != length) {
                    itemCount += getTitleCount(VIEW_TYPE_ORDER[i + 1]);
                }
            } else {
                itemCount += getItemCount(viewType);
            }
        }

        return itemCount;
    }

    public void removePhoneNumber(final int position, final boolean update) {
        val phoneNumberItems = getPhoneNumberItems();
        final int removeNumberPosition = position - getItemsPositionByType(VIEW_TYPE_PHONE_NUMBER);
        phoneNumberItems.remove(phoneNumberItems.keyAt(removeNumberPosition));
        if (update) {
            notifyItemRemoved(position);
            if (removeNumberPosition == phoneNumberItems.size()) {
                notifyItemChanged(position - 1);
            }
        }
    }

    public void removeSubwayItem(final int position, final boolean update) {
        val subwayItems = getSubwayItems();
        final int removeSubwayPosition = position - getItemsPositionByType(VIEW_TYPE_SUBWAY);
        subwayItems.remove(subwayItems.keyAt(removeSubwayPosition));
        if (update) {
            notifyItemRemoved(position);
        }
    }

    public void setCityItem(@NonNull final CityItem cityItem, final boolean notifyAdapter) {
        Contracts.requireNonNull(cityItem, "cityItem == null");

        _cityItem = cityItem;

        if (notifyAdapter) {
            notifyItemChanged(getItemsPositionByType(cityItem.getItemType()));
        }
    }

    public void setNameItem(@NonNull final NameItem nameItem, final boolean notifyAdapter) {
        Contracts.requireNonNull(nameItem, "nameItem == null");

        _nameItem = nameItem;

        if (notifyAdapter) {
            notifyItemChanged(getItemsPositionByType(nameItem.getItemType()));
        }
    }

    public void setPhotosItem(@NonNull final PhotosItem photosItem, final boolean notifyAdapter) {
        _photosItem = photosItem;

        if (notifyAdapter) {
            notifyItemChanged(getItemsPositionByType(photosItem.getItemType()) + 1);
        }
    }

    public void setPlaceItem(@NonNull final PlaceItem placeItem, final boolean notifyAdapter) {
        Contracts.requireNonNull(placeItem, "placeItem == null");

        _placeItem = placeItem;

        if (notifyAdapter) {
            notifyItemChanged(getItemsPositionByType(placeItem.getItemType()));
        }
    }

    @NonNull
    protected GeneralInfoItem getTitle(final int nextType) {
        final GeneralInfoItem item;

        if (nextType == VIEW_TYPE_PHONE_NUMBER) {
            item = new TitleItem(R.string.playground_editor_general_info_phone_number_title);
        } else if (nextType == VIEW_TYPE_CITY) {
            item = new TitleItem(R.string.playground_editor_general_info_city_title);
        } else if (nextType == VIEW_TYPE_SUBWAY) {
            item = new TitleItem(R.string.playground_editor_general_info_subways_title);
        } else if (nextType == VIEW_TYPE_PHOTOS) {
            item = new TitleItem(R.string.playground_editor_general_info_photo_title);
        } else {
            throw new IllegalArgumentException("Unknown next view type: " + nextType);
        }

        return item;
    }

    protected void onBindCityViewHolder(
        final CityViewHolder cityViewHolder, final CityItem cityItem) {
        Contracts.requireNonNull(cityViewHolder, "cityViewHolder == null");
        Contracts.requireNonNull(cityItem, "cityItem == null");

        val cities = cityItem.getCities();

        if (cities != null) {
            val adapter = (CityAdapter) cityViewHolder.citySpinner.getAdapter();
            if (adapter != null) {
                adapter.setNotifyOnChange(false);
                adapter.clear();
                adapter.addAll(cities.values());
                adapter.sort(new Comparator<City>() {
                    @Override
                    public int compare(final City o1, final City o2) {
                        val name = o1.getName();
                        val name1 = o2.getName();
                        if (name != null && name1 != null) {
                            return name.compareToIgnoreCase(name1);
                        }
                        return 0;
                    }
                });
                adapter.notifyDataSetChanged();

                val selectedCityId = cityItem.getSelectedCityId();
                if (selectedCityId != null) {
                    cityViewHolder.citySpinner.setSelection(adapter.getPosition(cities.get(
                        selectedCityId)));
                }
            }
        }
    }

    protected void onBindNameViewHolder(
        @NonNull final NameViewHolder nameViewHolder, @NonNull final NameItem nameItem) {
        Contracts.requireNonNull(nameViewHolder, "nameViewHolder == null");
        Contracts.requireNonNull(nameItem, "nameItem == null");

        val textWatcher = nameViewHolder.getTextWatcher();

        nameViewHolder._nameInput.removeTextChangedListener(textWatcher);
        val name = nameItem.getName();
        nameViewHolder._nameInput.setText(name);
        if (name != null) {
            nameViewHolder._nameInput.setSelection(name.length());
        }
        nameViewHolder._nameInput.addTextChangedListener(textWatcher);

        if (nameItem.isShowError()) {
            nameViewHolder._nameInputLayout.setError(nameViewHolder
                                                         .getContext()
                                                         .getString(nameItem.getError()));
        } else {
            nameViewHolder._nameInputLayout.setError(null);
        }
    }

    protected void onBindPhoneNumberViewHolder(
        @NonNull final PhoneNumberViewHolder phoneNumberViewHolder,
        @NonNull final PhoneNumberItem phoneNumberItem) {
        Contracts.requireNonNull(phoneNumberViewHolder, "phoneNumberViewHolder == null");
        Contracts.requireNonNull(phoneNumberItem, "phoneNumberItem == null");

        phoneNumberViewHolder._phoneNumber.setEnabled(false);
        phoneNumberViewHolder._phoneNumber.setEnabled(true);

        val phoneTextWatcher = phoneNumberViewHolder.getPhoneTextWatcher();
        val phoneNumberItems = getPhoneNumberItems();
        phoneTextWatcher.setItemId(phoneNumberItems.keyAt(phoneNumberItems.indexOfValue(
            phoneNumberItem)));

        phoneTextWatcher.setListen(false);
        val phoneNumber = phoneNumberItem.getPhoneNumber();
        phoneNumberViewHolder._phoneNumber.setText(phoneNumber);
        if (phoneNumber != null) {
            phoneNumberViewHolder._phoneNumber.setSelection(phoneNumber.length());
        }
        phoneTextWatcher.setListen(true);
    }

    protected void onBindPhotosViewHolder(
        @NonNull final PhotosViewHolder photosViewHolder, @NonNull final PhotosItem photosItem) {
        Contracts.requireNonNull(photosViewHolder, "photosViewHolder == null");
        Contracts.requireNonNull(photosItem, "photosItem == null");

        val adapter = (PhotosAdapter) photosViewHolder.photosList.getAdapter();

        if (adapter != null) {
            adapter.setPhotoItems(photosItem.getPhotoItems());
            adapter.notifyDataSetChanged();
        }
    }

    protected void onBindPlaceViewHolder(
        @NonNull final PlaceViewHolder placeViewHolder, @NonNull final PlaceItem placeItem) {
        Contracts.requireNonNull(placeViewHolder, "placeViewHolder == null");
        Contracts.requireNonNull(placeItem, "placeItem == null");

        placeViewHolder.mapLayout.setVisibility(View.GONE);

        val address = placeItem.getAddress();
        if (!TextUtils.isEmpty(address)) {
            placeViewHolder.addressInput.setText(address);
        }

        val latitude = placeItem.getLatitude();
        val longitude = placeItem.getLongitude();

        if (latitude != null && longitude != null) {
            val context = placeViewHolder.getContext();
            val numberFormat = NumberFormat.getInstance();
            numberFormat.setMaximumFractionDigits(6);
            val coordinates =
                context.getString(R.string.playground_editor_general_info_lat_lng_format,
                                  numberFormat.format(latitude.doubleValue()),
                                  numberFormat.format(longitude.doubleValue()));
            placeViewHolder.coordinatesInput.setText(coordinates);

            initializeErrorView(placeViewHolder.mapError);
            val loadingViewDelegate = LoadingViewDelegate
                .builder()
                .setContentView(placeViewHolder.mapLayout)
                .setLoadingView(placeViewHolder.mapProgressBar)
                .setErrorView(placeViewHolder.mapError)
                .setLoadingVisibilityHandler(new ProgressVisibilityHandler())
                .build();
            loadingViewDelegate.showLoading();

            placeViewHolder.mapLayout.setVisibility(View.VISIBLE);

            placeViewHolder.mapView.post(new Runnable() {
                @Override
                public void run() {
                    initializeMap(latitude,
                                  longitude,
                                  placeViewHolder.mapView,
                                  loadingViewDelegate);
                }
            });
        }
    }

    protected void onBindSubwaysViewHolder(
        @NonNull final SubwayViewHolder subwayViewHolder, @NonNull final SubwayItem subwayItem) {
        Contracts.requireNonNull(subwayViewHolder, "subwayViewHolder == null");
        Contracts.requireNonNull(subwayItem, "subwaysItem == null");

        val subwayItems = getSubwayItems();
        subwayViewHolder
            .getSubwayOnItemClick()
            .setItemId(subwayItems.keyAt(subwayItems.indexOfValue(subwayItem)));

        val subways = subwayItem.getSubways();
        val selectedSubwayId = subwayItem.getSelectedSubwayId();

        val adapter = (SubwayAdapter) subwayViewHolder._subwayInput.getAdapter();
        if (adapter != null) {
            if (subways != null && selectedSubwayId == null) {
                adapter.setSubways(subways.values());
            } else {
                adapter.setSubways(null);
            }
        }

        if (subways != null && selectedSubwayId != null) {
            subwayViewHolder._subwayInput.setEnabled(false);
            subwayViewHolder._subwayInput.setText(subways.get(selectedSubwayId).getName());
        } else {
            subwayViewHolder._subwayInput.setEnabled(true);
            subwayViewHolder._subwayInput.setText(null);
        }
    }

    protected void onBindTitleViewHolder(
        @NonNull final TitleViewHolder titleViewHolder, @NonNull final TitleItem titleItem) {
        Contracts.requireNonNull(titleViewHolder, "titleViewHolder == null");
        Contracts.requireNonNull(titleItem, "titleItem == null");

        titleViewHolder._titleView.setText(titleViewHolder
                                               .getContext()
                                               .getString(titleItem.getTitle()));
    }

    protected ExtendedRecyclerViewHolder onCreateCityViewHolder(@NonNull final ViewGroup parent) {
        Contracts.requireNonNull(parent, "parent == null");

        val context = parent.getContext();
        val inflater = LayoutInflater.from(context);

        val view = inflater.inflate(R.layout.fragment_playground_general_info_editor_city_item,
                                    parent,
                                    false);

        val cityViewHolder = new CityViewHolder(view);

        cityViewHolder.citySpinner.setAdapter(new CityAdapter(context));

        cityViewHolder.citySpinner.setOnItemSelectedListener(new AdapterView
            .OnItemSelectedListener() {
            @Override
            public void onItemSelected(
                final AdapterView<?> parent, final View view, final int position, final long id) {
                getCityItem().setSelectedCityId(id);
                clearSubways();
                _updateSubwaysEvent.rise(new UpdateSubwaysEventArgs(id));
            }

            @Override
            public void onNothingSelected(final AdapterView<?> parent) {
            }
        });

        return cityViewHolder;
    }

    @NonNull
    protected NameViewHolder onCreateNameViewHolder(@NonNull final ViewGroup parent) {
        Contracts.requireNonNull(parent, "parent == null");

        val inflater = LayoutInflater.from(parent.getContext());

        val view = inflater.inflate(R.layout.fragment_playground_general_info_editor_name_item,
                                    parent,
                                    false);

        val watcher = new SimpleTextWatcher() {
            @Override
            public void onTextChanged(
                final CharSequence s, final int start, final int before, final int count) {
                super.onTextChanged(s, start, before, count);

                val nameItem = getNameItem();
                nameItem.setName(s.toString());

                if (nameItem.isShowError()) {
                    nameItem.clearError();
                    notifyItemChanged(getItemsPositionByType(nameItem.getItemType()));
                }
            }
        };

        return new NameViewHolder(view, watcher);
    }

    @NonNull
    protected PhoneNumberViewHolder onCreatePhoneNumberViewHolder(@NonNull final ViewGroup parent) {
        Contracts.requireNonNull(parent, "parent == null");

        val inflater = LayoutInflater.from(parent.getContext());

        val view = inflater.inflate(R.layout.fragment_playground_general_info_editor_phone_item,
                                    parent,
                                    false);

        val phoneTextWatcher = new PhoneTextWatcher();
        val phoneNumberViewHolder = new PhoneNumberViewHolder(view, phoneTextWatcher);

        phoneNumberViewHolder._phoneNumber.addTextChangedListener(phoneTextWatcher);

        return phoneNumberViewHolder;
    }

    @NonNull
    protected ExtendedRecyclerViewHolder onCreatePhotosViewHolder(@NonNull final ViewGroup parent) {
        Contracts.requireNonNull(parent, "parent == null");

        val context = parent.getContext();
        val inflater = LayoutInflater.from(context);

        val view = inflater.inflate(R.layout.fragment_playground_general_info_editor_photos_item,
                                    parent,
                                    false);

        val photosViewHolder = new PhotosViewHolder(view);

        photosViewHolder.photosList.setLayoutManager(new LinearLayoutManager(context,
                                                                             LinearLayoutManager
                                                                                 .HORIZONTAL,
                                                                             false));

        _photosAdapter = new PhotosAdapter();
        _photosAdapter.getPickPhotoEvent().addHandler(_pickPhotoHandler);
        photosViewHolder.photosList.setAdapter(_photosAdapter);

        return photosViewHolder;
    }

    @NonNull
    protected PlaceViewHolder onCreatePlaceViewHolder(@NonNull final ViewGroup parent) {
        Contracts.requireNonNull(parent, "parent == null");

        val inflater = LayoutInflater.from(parent.getContext());

        val view = inflater.inflate(R.layout.fragment_playground_general_info_editor_address_item,
                                    parent,
                                    false);

        val placeViewHolder = new PlaceViewHolder(view);

        placeViewHolder.coordinatesInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                _pickLocationEvent.rise(new PlacePickerEventArgs(getPlaceItem().getViewPort()));
            }
        });

        placeViewHolder.addressInput.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(
                final CharSequence s, final int start, final int before, final int count) {
                super.onTextChanged(s, start, before, count);

                getPlaceItem().setAddress(s.toString());
            }
        });

        return placeViewHolder;
    }

    @NonNull
    protected ExtendedRecyclerViewHolder onCreateSubwaysViewHolder(
        @NonNull final ViewGroup parent) {
        Contracts.requireNonNull(parent, "parent == null");

        val context = parent.getContext();
        val inflater = LayoutInflater.from(context);

        val view = inflater.inflate(R.layout.fragment_playground_general_info_editor_subways_item,
                                    parent,
                                    false);

        val subwayOnItemClick = new SubwayOnItemClick();
        val subwaysViewHolder = new SubwayViewHolder(view, subwayOnItemClick);

        subwaysViewHolder._subwayInput.setAdapter(new SubwayAdapter(context));
        subwaysViewHolder._subwayInput.setOnItemClickListener(subwayOnItemClick);

        return subwaysViewHolder;
    }

    @NonNull
    protected ExtendedRecyclerViewHolder onCreateTitleViewHolder(@NonNull final ViewGroup parent) {
        Contracts.requireNonNull(parent, "parent == null");

        val inflater = LayoutInflater.from(parent.getContext());

        val view = inflater.inflate(R.layout.fragment_playground_general_info_editor_title_item,
                                    parent,
                                    false);

        return new TitleViewHolder(view);
    }

    @Getter
    @NonNull
    private final SparseArray<PhoneNumberItem> _phoneNumberItems = new SparseArray<>();

    @Getter
    @NonNull
    private final ManagedEvent<PlacePickerEventArgs> _pickLocationEvent = Events.createEvent();

    @NonNull
    private final ManagedNoticeEvent _pickPhotoEvent = Events.createNoticeEvent();

    private final NoticeEventHandler _pickPhotoHandler = new NoticeEventHandler() {
        @Override
        public void onEvent() {
            _pickPhotoEvent.rise();
        }
    };

    @Getter
    @NonNull
    private final SparseArray<SubwayItem> _subwayItems = new SparseArray<>();

    @Getter
    @NonNull
    private final ManagedEvent<UpdateSubwaysEventArgs> _updateSubwaysEvent = Events.createEvent();

    @Getter
    @NonNull
    private CityItem _cityItem = new CityItem();

    @Getter
    @NonNull
    private NameItem _nameItem = new NameItem();

    @Nullable
    private PhotosAdapter _photosAdapter;

    @Getter
    @NonNull
    private PhotosItem _photosItem = new PhotosItem();

    @Getter
    @NonNull
    private PlaceItem _placeItem = new PlaceItem();

    private void displayDivider(final RecyclerView.ViewHolder viewHolder, final boolean display) {
        DividerItemDecoration.setDecorationMode(viewHolder.itemView,
                                                display
                                                ? DividerItemDecoration.DecorationMode.ALL
                                                : DividerItemDecoration.DecorationMode.NONE);
    }

    private int getNextId(@NonNull final SparseArray<?> sparseArray) {
        Contracts.requireNonNull(sparseArray, "sparseArray == null");

        int id = -1;
        for (int i = 0; i < sparseArray.size(); i++) {
            final int key = sparseArray.keyAt(i);
            if (id < key) {
                id = key;
            }
        }
        return id + 1;
    }

    private int getPositionNotFocusedEmptyPhoneItem(
        @NonNull final GeneralInfoItem focusedPhoneNumberItem) {
        Contracts.requireNonNull(focusedPhoneNumberItem, "focusedPhoneNumberItem == null");

        final int itemsPositionByType =
            getItemsPositionByType(focusedPhoneNumberItem.getItemType());

        int position = 0;
        final val phoneNumberItems = getPhoneNumberItems();
        for (int i = 0; i < phoneNumberItems.size(); i++) {
            val phoneNumberItem = phoneNumberItems.valueAt(i);
            if (TextUtils.isEmpty(phoneNumberItem.getPhoneNumber()) &&
                !focusedPhoneNumberItem.equals(phoneNumberItem)) {
                position = phoneNumberItems.indexOfValue(phoneNumberItem);
            }
        }

        return itemsPositionByType + position;
    }

    private void initializeErrorView(@NonNull final TextView errorView) {
        Contracts.requireNonNull(errorView, "errorView == null");

        final val context = errorView.getContext();
        final val errorIcon = ContextCompat.getDrawable(context, R.drawable.ic_material_error);
        final val tintList =
            ThemeUtils.resolveColorStateList(context, android.R.attr.textColorSecondary);
        if (tintList != null) {
            DrawableCompat.setTint(errorIcon, tintList.getDefaultColor());
        }
        errorView.setCompoundDrawablesRelativeWithIntrinsicBounds(errorIcon, null, null, null);
    }

    private void initializeMap(
        @NonNull final Double latitude,
        @NonNull final Double longitude,
        @NonNull final ImageView mapView,
        @NonNull final LoadingViewDelegate loadingView) {
        Contracts.requireNonNull(latitude, "latitude == null");
        Contracts.requireNonNull(longitude, "longitude == null");
        Contracts.requireNonNull(mapView, "mapView == null");
        Contracts.requireNonNull(loadingView, "loadingView == null");

        val url = String.format(Locale.US,
                                "http://maps.googleapis.com/maps/api/staticmap?" +
                                "center=%s,%s&zoom=18&size=%dx%d&maptype=roadmap%%20",
                                latitude,
                                longitude,
                                mapView.getWidth(),
                                mapView.getHeight());

        Glide
            .with(mapView.getContext())
            .load(url)
            .centerCrop()
            .animate(R.anim.fade_in_long)
            .listener(new RequestListener<String, GlideDrawable>() {
                @Override
                public boolean onException(
                    final Exception e,
                    final String model,
                    final Target<GlideDrawable> target,
                    final boolean isFirstResource) {

                    loadingView.showError();

                    return false;
                }

                @Override
                public boolean onResourceReady(
                    final GlideDrawable resource,
                    final String model,
                    final Target<GlideDrawable> target,
                    final boolean isFromMemoryCache,
                    final boolean isFirstResource) {

                    loadingView.showContent();

                    return false;
                }
            })
            .into(mapView);
    }

    public class PhoneTextWatcher extends SimpleTextWatcher {
        @Getter
        @Setter
        private int _itemId;

        @Getter
        @Setter
        private boolean _listen;

        @Override
        public void onTextChanged(
            final CharSequence s, final int start, final int before, final int count) {
            super.onTextChanged(s, start, before, count);

            if (!isListen()) {
                return;
            }

            val item = getPhoneNumberItems().get(_itemId);

            val beforeValue = item.getPhoneNumber();

            item.setPhoneNumber(s.toString());

            if (TextUtils.isEmpty(beforeValue) && count > 0) {
                addPhoneItem(new PhoneNumberItem(), true);
            } else if (!TextUtils.isEmpty(beforeValue) && before == beforeValue.length()) {
                removePhoneNumber(getPositionNotFocusedEmptyPhoneItem(item), true);
            }
        }
    }

    public class SubwayOnItemClick implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(
            final AdapterView<?> parent, final View view, final int position, final long id) {

            final int itemId = getItemId();
            val subwayItems = getSubwayItems();
            val subwayItem = subwayItems.get(itemId);

            subwayItem.setSelectedSubwayId(((SubwayStation) parent.getItemAtPosition(position))
                                               .getId());

            notifyItemChanged(
                getItemsPositionByType(subwayItem.getItemType()) + subwayItems.indexOfKey(itemId));

            val newSubwayItem = new SubwayItem();
            newSubwayItem.setSubways(subwayItems.valueAt(0).getSubways());
            addSubwayItems(Collections.singletonList(newSubwayItem), true);
        }

        @Setter
        @Getter
        private int _itemId;
    }
}
