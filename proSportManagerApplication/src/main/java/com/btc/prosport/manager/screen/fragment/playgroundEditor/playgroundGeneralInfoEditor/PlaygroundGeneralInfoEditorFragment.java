package com.btc.prosport.manager.screen.fragment.playgroundEditor.playgroundGeneralInfoEditor;

import lombok.experimental.Accessors;

@Accessors(prefix = "_")
public class PlaygroundGeneralInfoEditorFragment
   /* extends BasePlaygroundEditorPageFragment
    implements PlaygroundGeneralInfoEditorScreen*/
{
//    private static final String _KEY_SAVED_STATE =
//        ConstantBuilder.savedStateKey(PlaygroundGeneralInfoEditorFragment.class, "saved_state");
//
//    @Override
//    public void displayCity(@NonNull final List<City> cities) {
//        Contracts.requireNonNull(cities, "cities == null");
//
//        val cityItem = new CityItem();
//
//        val citiesMap = new HashMap<Long, City>(cities.size());
//        for (final val city : cities) {
//            citiesMap.put(city.getId(), city);
//        }
//
//        cityItem.setCities(citiesMap);
//        if (_state != null) {
//            val cityId = _state.getCityId();
//            if (cityId != null) {
//                cityItem.setSelectedCityId(cityId);
//            }
//        }
//        val generalInfoAdapter = getGeneralInfoAdapter();
//        if (generalInfoAdapter != null) {
//            generalInfoAdapter.setCityItem(cityItem, true);
//        }
//    }
//
//    @Override
//    public void displayPhoto(@NonNull final PhotoResult photoResult) {
//        Contracts.requireNonNull(photoResult, "photoResult == null");
//
//        val item = new PhotoItem(photoResult.getUri());
//        val generalInfoAdapter = getGeneralInfoAdapter();
//        if (generalInfoAdapter != null) {
//            generalInfoAdapter.addPhotoItems(Collections.singletonList(item), true);
//        }
//    }
//
//    @Override
//    public void displayPickedLocation(@NonNull final PlacePickerResult placePickerResult) {
//        Contracts.requireNonNull(placePickerResult, "placePickerResult == null");
//
//        if (_generalInfoAdapter != null) {
//            val placeItem = new PlaceItem();
//            placeItem.setPlaceId(placePickerResult.getPlaceId());
//            placeItem.setAddress(placePickerResult.getAddress());
//            placeItem.setLatitude(placePickerResult.getLatitude());
//            placeItem.setLongitude(placePickerResult.getLongitude());
//            placeItem.setViewPort(placePickerResult.getViewPort());
//
//            _generalInfoAdapter.setPlaceItem(placeItem, true);
//        }
//    }
//
//    @Override
//    public void displaySubways(@NonNull final List<SubwayStation> subways) {
//        Contracts.requireNonNull(subways, "subways == null");
//
//        val generalInfoAdapter = getGeneralInfoAdapter();
//        if (generalInfoAdapter != null && !subways.isEmpty()) {
//
//            val subwaysMap = new HashMap<Long, SubwayStation>(subways.size());
//            for (final val subway : subways) {
//                subwaysMap.put(subway.getId(), subway);
//            }
//
//            List<SubwayItem> subwayItems = null;
//            if (_state != null) {
//                val subwayIdList = _state.getSubwayIdList();
//                if (subwayIdList != null) {
//                    subwayItems = new ArrayList<>(subwayIdList.size());
//                    for (final val id : subwayIdList) {
//                        val subwayItem = new SubwayItem();
//                        subwayItem.setSubways(subwaysMap);
//                        subwayItem.setSelectedSubwayId(id);
//                        subwayItems.add(subwayItem);
//                    }
//                }
//            }
//
//            if (subwayItems == null) {
//                val subwayItem = new SubwayItem();
//                subwayItem.setSubways(subwaysMap);
//                subwayItems = Collections.singletonList(subwayItem);
//            }
//
//            generalInfoAdapter.addSubwayItems(subwayItems, true);
//        }
//    }
//
//    @Override
//    public NoticeEvent getCitiesUpdateEvent() {
//        return _updateCitiesEvent;
//    }
//
//    @NonNull
//    @Override
//    public Event<PlacePickerEventArgs> getPickLocationEvent() {
//        val generalInfoAdapter = getGeneralInfoAdapter();
//        if (generalInfoAdapter != null) {
//            return generalInfoAdapter.getPickLocationEvent();
//        } else {
//            throw new NullPointerException("generalInfoAdapter == null");
//        }
//    }
//
//    @NonNull
//    @Override
//    public NoticeEvent getPickPhotoEvent() {
//        val generalInfoAdapter = getGeneralInfoAdapter();
//        if (generalInfoAdapter != null) {
//            return generalInfoAdapter.getPickPhotoEvent();
//        } else {
//            throw new NullPointerException("generalInfoAdapter == null");
//        }
//    }
//
//    @NonNull
//    @Override
//    public Event<UpdateSubwaysEventArgs> getSubwaysUpdateEvent() {
//        val generalInfoAdapter = getGeneralInfoAdapter();
//        if (generalInfoAdapter != null) {
//            return generalInfoAdapter.getUpdateSubwaysEvent();
//        } else {
//            throw new NullPointerException("generalInfoAdapter == null");
//        }
//    }
//
//    @Nullable
//    @Override
//    public View onCreateView(
//        final LayoutInflater inflater,
//        @Nullable final ViewGroup container,
//        @Nullable final Bundle savedInstanceState) {
//        super.onCreateView(inflater, container, savedInstanceState);
//
//        if (savedInstanceState != null) {
//            _state = onHandleSavedState(savedInstanceState);
//        }
//
//        val arguments = getArguments();
//        if (_state == null && arguments != null) {
//            _state = onHandleArguments(arguments);
//        }
//
//        val view =
//            inflater.inflate(R.layout.fragment_playground_general_info_editor, container, false);
//
//        bindViews(view);
//
//        _layoutManager = new LinearLayoutManager(getContext());
//
//        _generalInfoAdapter = new GeneralInfoAdapter();
//
//        initializeSwipeToDeletePhoneNumbers();
//        if (_state != null) {
//            restoreAdapter();
//        }
//
//        if (_generalInfoList != null) {
//            val resources = getResources();
//            final int spacing =
//                resources.getDimensionPixelSize(R.dimen.playground_general_info_editor_spacing);
//            final int padding =
//                resources.getDimensionPixelSize(R.dimen.playground_general_info_editor_padding);
//            val spacingDecoration = SpacingItemDecoration
//                .builder()
//                .setVerticalSpacing(spacing, spacing)
//                .setPadding(padding, padding, padding, padding)
//                .build();
//            _generalInfoList.addItemDecoration(spacingDecoration);
//            _generalInfoList.addItemDecoration(new DividerItemDecoration(getContext(),
//                                                                         DividerItemDecoration
//                                                                             .VERTICAL));
//            _generalInfoList.setLayoutManager(_layoutManager);
//            _generalInfoList.setAdapter(_generalInfoAdapter);
//        }
//
//        return view;
//    }
//
//    @Override
//    public void onSaveInstanceState(final Bundle outState) {
//        super.onSaveInstanceState(outState);
//
//        if (outState != null) {
//            if (_state == null) {
//                _state = new PlaygroundGeneralInfoEditorState();
//            }
//
//            val generalInfoAdapter = getGeneralInfoAdapter();
//            if (generalInfoAdapter != null) {
//                _state.setPlaygroundName(generalInfoAdapter.getNameItem().getName());
//
//                val placeItem = generalInfoAdapter.getPlaceItem();
//                _state.setLongitude(placeItem.getLongitude());
//                _state.setLatitude(placeItem.getLatitude());
//                _state.setAddress(placeItem.getAddress());
//                _state.setPlaceId(placeItem.getPlaceId());
//                _state.setViewPort(placeItem.getViewPort());
//
//                _state.setCityId(generalInfoAdapter.getCityItem().getSelectedCityId());
//
//                val phoneNumberItems = generalInfoAdapter.getPhoneNumberItems();
//                final int phonesSize = phoneNumberItems.size();
//                if (phonesSize > 0) {
//                    val phoneNumbers = new ArrayList<String>(phonesSize);
//                    for (int i = 0; i < phonesSize; i++) {
//                        phoneNumbers.add(phoneNumberItems.valueAt(i).getPhoneNumber());
//                    }
//                    _state.setPhoneNumberList(phoneNumbers);
//                }
//
//                _state.setCityId(generalInfoAdapter.getCityItem().getSelectedCityId());
//
//                val subwayItems = generalInfoAdapter.getSubwayItems();
//                final int subwaysSize = subwayItems.size();
//                if (subwaysSize > 0) {
//                    val subwaysIds = new ArrayList<Long>(subwaysSize);
//                    for (int i = 0; i < subwaysSize; i++) {
//                        subwaysIds.add(subwayItems.valueAt(i).getSelectedSubwayId());
//                    }
//                    _state.setSubwayIdList(subwaysIds);
//                }
//
//                val photosItem = generalInfoAdapter.getPhotosItem();
//                val photoItems = photosItem.getPhotoItems();
//                if (!photoItems.isEmpty()) {
//                    val uriList = new ArrayList<Uri>(photoItems.size());
//                    for (final val photoItem : photoItems) {
//                        uriList.add(photoItem.getUri());
//                    }
//                    _state.setPhotoUriList(uriList);
//                }
//            }
//
//            outState.putParcelable(_KEY_SAVED_STATE, Parcels.wrap(_state));
//        }
//    }
//
//    @Override
//    protected void onReleaseInjectedMembers() {
//        super.onReleaseInjectedMembers();
//
//        final val presenter = getPresenter();
//        if (presenter != null) {
//            presenter.unbindScreen();
//        }
//    }
//
//    // TODO: Validate remaining data.
//    @Override
//    public boolean validateData() {
//        boolean isValid = false;
//
//        val generalInfoAdapter = getGeneralInfoAdapter();
//        if (generalInfoAdapter != null) {
//            val nameItem = generalInfoAdapter.getNameItem();
//            nameItem.validate();
//            if (!nameItem.isShowError()) {
//                isValid = true;
//            } else {
//                generalInfoAdapter.setNameItem(nameItem, true);
//            }
//        }
//
//        return isValid;
//    }
//
//    @Override
//    protected void onEnterPage() {
//        super.onEnterPage();
//
//        boolean citiesLoaded = false;
//        val generalInfoAdapter = getGeneralInfoAdapter();
//        if (generalInfoAdapter != null) {
//            val cities = generalInfoAdapter.getCityItem().getCities();
//            if (cities != null && !cities.isEmpty()) {
//                citiesLoaded = true;
//            }
//        }
//
//        if (!citiesLoaded) {
//            _updateCitiesEvent.rise();
//        }
//    }
//
//    @CallSuper
//    @Nullable
//    protected PlaygroundGeneralInfoEditorState onHandleArguments(@NonNull final Bundle arguments) {
//        Contracts.requireNonNull(arguments, "arguments == null");
//
//        return null;
//    }
//
//    @CallSuper
//    @Nullable
//    protected PlaygroundGeneralInfoEditorState onHandleSavedState(
//        @NonNull final Bundle savedInstanceState) {
//        Contracts.requireNonNull(savedInstanceState, "savedInstanceState == null");
//
//        PlaygroundGeneralInfoEditorState state = null;
//
//        if (savedInstanceState.containsKey(_KEY_SAVED_STATE)) {
//            state = Parcels.unwrap(savedInstanceState.getParcelable(_KEY_SAVED_STATE));
//        }
//
//        return state;
//    }
//
//    @Override
//    protected void onInjectMembers() {
//        super.onInjectMembers();
//
//        getManagerSubscreenComponent().inject(this);
//
//        final val presenter = getPresenter();
//        if (presenter != null) {
//            presenter.bindScreen(this);
//        }
//    }
//
//    @BindView(R.id.general_info_list)
//    @Nullable
//    /*package-private*/ RecyclerView _generalInfoList;
//
//    @Named(PresenterNames.PLAYGROUND_GENERAL_INFO_EDITOR)
//    @Inject
//    @Getter(AccessLevel.PROTECTED)
//    @Nullable
//    /*package-private*/ Presenter<PlaygroundGeneralInfoEditorScreen> _presenter;
//
//    private final ManagedNoticeEvent _updateCitiesEvent = Events.createNoticeEvent();
//
//    @Getter(AccessLevel.PROTECTED)
//    @Nullable
//    private GeneralInfoAdapter _generalInfoAdapter;
//
//    @Getter(AccessLevel.PROTECTED)
//    @Nullable
//    private LinearLayoutManager _layoutManager;
//
//    @Nullable
//    private PlaygroundGeneralInfoEditorState _state;
//
//    private void initializeSwipeToDeletePhoneNumbers() {
//        val itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.Callback() {
//
//            @Override
//            public int getMovementFlags(
//                final RecyclerView recyclerView, final RecyclerView.ViewHolder viewHolder) {
//
//                boolean swipe = false;
//                val generalInfoAdapter = getGeneralInfoAdapter();
//                if (generalInfoAdapter != null) {
//                    if (viewHolder.getItemViewType() == GeneralInfoAdapter.VIEW_TYPE_PHONE_NUMBER) {
//                        final int adapterPosition = viewHolder.getAdapterPosition();
//                        val item = (PhoneNumberItem) generalInfoAdapter.getItem(adapterPosition);
//                        if (!TextUtils.isEmpty(item.getPhoneNumber())) {
//                            swipe = true;
//                        }
//                    } else if (viewHolder.getItemViewType() ==
//                               GeneralInfoAdapter.VIEW_TYPE_SUBWAY) {
//                        final int adapterPosition = viewHolder.getAdapterPosition();
//                        val item = (SubwayItem) generalInfoAdapter.getItem(adapterPosition);
//                        if (item.getSelectedSubwayId() != null) {
//                            swipe = true;
//                        }
//                    }
//                }
//
//                if (swipe) {
//                    return makeMovementFlags(0, ItemTouchHelper.START | ItemTouchHelper.END);
//                } else {
//                    return 0;
//                }
//            }
//
//            @Override
//            public boolean onMove(
//                final RecyclerView recyclerView,
//                final RecyclerView.ViewHolder viewHolder,
//                final RecyclerView.ViewHolder target) {
//                return false;
//            }
//
//            @Override
//            public void onSwiped(final RecyclerView.ViewHolder viewHolder, final int direction) {
//                val generalInfoAdapter = getGeneralInfoAdapter();
//                if (generalInfoAdapter != null) {
//                    final int itemViewType = viewHolder.getItemViewType();
//                    if (itemViewType == GeneralInfoAdapter.VIEW_TYPE_PHONE_NUMBER) {
//                        generalInfoAdapter.removePhoneNumber(viewHolder.getAdapterPosition(), true);
//                    } else if (itemViewType == GeneralInfoAdapter.VIEW_TYPE_SUBWAY) {
//                        generalInfoAdapter.removeSubwayItem(viewHolder.getAdapterPosition(), true);
//                    }
//                }
//            }
//        });
//        itemTouchHelper.attachToRecyclerView(_generalInfoList);
//    }
//
//    // TODO: Restore city and subways.
//    private void restoreAdapter() {
//        val generalInfoAdapter = getGeneralInfoAdapter();
//
//        if (_state != null && generalInfoAdapter != null) {
//
//            val playgroundName = _state.getPlaygroundName();
//            if (playgroundName != null) {
//                val nameItem = new NameItem();
//                nameItem.setName(playgroundName);
//                generalInfoAdapter.setNameItem(nameItem, false);
//            }
//
//            val placeId = _state.getPlaceId();
//            if (placeId != null) {
//                val placeItem = new PlaceItem();
//                placeItem.setPlaceId(placeId);
//                placeItem.setAddress(_state.getAddress());
//                placeItem.setLatitude(_state.getLatitude());
//                placeItem.setLongitude(_state.getLongitude());
//                placeItem.setViewPort(_state.getViewPort());
//                generalInfoAdapter.setPlaceItem(placeItem, false);
//            }
//
//            val phoneNumberList = _state.getPhoneNumberList();
//            if (phoneNumberList != null && phoneNumberList.size() > 1) {
//                generalInfoAdapter.clearPhoneItems(false);
//                for (final val phoneNumber : phoneNumberList) {
//                    val phoneNumberItem = new PhoneNumberItem();
//                    phoneNumberItem.setPhoneNumber(phoneNumber);
//                    generalInfoAdapter.addPhoneItem(phoneNumberItem, false);
//                }
//            }
//
//            val photoUriList = _state.getPhotoUriList();
//            if (photoUriList != null && !photoUriList.isEmpty()) {
//                val photoItems = new ArrayList<PhotoItem>(photoUriList.size());
//                for (final val photoUri : photoUriList) {
//                    photoItems.add(new PhotoItem(photoUri));
//                }
//                val photosItem = new PhotosItem();
//                photosItem.setPhotoItems(photoItems);
//                generalInfoAdapter.setPhotosItem(photosItem, false);
//            }
//
//            generalInfoAdapter.notifyDataSetChanged();
//        }
//    }
}
