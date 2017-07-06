package com.btc.prosport.manager.screen.fragment;

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.val;

import com.btc.common.ConstantBuilder;
import com.btc.common.event.Events;
import com.btc.common.event.generic.Event;
import com.btc.common.event.generic.ManagedEvent;
import com.btc.common.extension.delegate.loading.FadeVisibilityHandler;
import com.btc.common.extension.delegate.loading.LoadingViewDelegate;
import com.btc.common.extension.delegate.loading.ProgressVisibilityHandler;
import com.btc.common.extension.eventArgs.IdEventArgs;
import com.btc.common.extension.view.recyclerView.decoration.SpacingItemDecoration;
import com.btc.common.mvp.presenter.Presenter;
import com.btc.prosport.api.model.Attribute;
import com.btc.prosport.manager.R;
import com.btc.prosport.manager.core.adapter.attributeEditor.AttributeItem;
import com.btc.prosport.manager.core.adapter.attributeEditor.AttributesListAdapter;
import com.btc.prosport.manager.core.eventArgs.ChangePlaygroundAttributesEventArgs;
import com.btc.prosport.manager.di.qualifier.PresenterNames;
import com.btc.prosport.manager.screen.PlaygroundAttributesEditorScreen;
import com.btc.prosport.manager.screen.fragment.playgroundEditor.BasePlaygroundEditorPageFragment;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

@Accessors(prefix = "_")
public class PlaygroundAttributesEditorFragment extends BasePlaygroundEditorPageFragment
    implements PlaygroundAttributesEditorScreen {

    private static final String _LOG_TAG =
        ConstantBuilder.logTag(PlaygroundAttributesEditorFragment.class);

    @Override
    public void bindViews(@NonNull final View source) {
        super.bindViews(source);

        _attributesNoContentView = source.findViewById(R.id.no_content);
        _attributesLoadingErrorView = source.findViewById(R.id.loading_error);
        _attributesLoadingView = (ProgressBar) source.findViewById(R.id.loading);
        _attributesListView = (RecyclerView) source.findViewById(R.id.attributes);
    }

    @Nullable
    @Override
    public View onCreateView(
        final LayoutInflater inflater,
        @Nullable final ViewGroup container,
        @Nullable final Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        final val view =
            inflater.inflate(R.layout.fragment_playground_attributes_editor, container, false);

        bindViews(view);

        initAttributesListAdapter();

        _attributesLoadingViewDelegate = LoadingViewDelegate
            .builder()
            .setContentView(_attributesListView)
            .setLoadingView(_attributesLoadingView)
            .setNoContentView(_attributesNoContentView)
            .setErrorView(_attributesLoadingErrorView)
            .setContentVisibilityHandler(new FadeVisibilityHandler(R.anim.fade_in_long, 0))
            .setLoadingVisibilityHandler(new ProgressVisibilityHandler())
            .build();

        _attributesLoadingViewDelegate.showContent();

        return view;
    }

    @Override
    public void commitChanges() {

        final val playgroundId = getPlaygroundId();
        if (playgroundId != null) {
            final val attributesAdapter = getAttributesAdapter();
            if (attributesAdapter != null) {
                final val attributeIds = new ArrayList<Long>();
                final val currentAttributeList = attributesAdapter.getAttributeItems();
                for (final AttributeItem attribute : currentAttributeList) {
                    if (!attribute.isOff()) {
                        attributeIds.add(attribute.getId());
                    }
                }
                _changePlaygroundAttributesEvent.rise(new ChangePlaygroundAttributesEventArgs
                                                          (playgroundId,
                                                                                              attributeIds));
            }
        }
    }

    @Override
    public int getLabelId() {
        return R.string.playground_editor_attributes_title;
    }

    @Override
    public void displayLoading() {
        final val loadingViewDelegate = getAttributesLoadingViewDelegate();
        if (loadingViewDelegate != null) {
            loadingViewDelegate.showLoading();
        }
    }

    @Override
    public void displayLoadingError() {
        final val loadingViewDelegate = getAttributesLoadingViewDelegate();
        if (loadingViewDelegate != null) {
            loadingViewDelegate.showError();
        }
    }

    @Override
    public void displayPlaygroundAttributes(
        @NonNull final List<Attribute> playgroundAttributes,
        @NonNull final List<Attribute> allAttributes) {
        final val attributesAdapter = getAttributesAdapter();
        if (attributesAdapter != null) {
            final val currentAttributeList = attributesAdapter.getAttributeItems();
            if (!currentAttributeList.isEmpty()) {
                currentAttributeList.clear();
            }
            if (!allAttributes.isEmpty()) {
                setPlaygroundAttributes(playgroundAttributes);
                setAllAttributes(allAttributes);
                final val attributeItems =
                    createAttributeItems(allAttributes, playgroundAttributes);
                if (!attributeItems.isEmpty()) {
                    currentAttributeList.addAll(attributeItems);
                }
            }
            attributesAdapter.notifyDataSetChanged();
        }

        final val loadingViewDelegate = getAttributesLoadingViewDelegate();
        if (loadingViewDelegate != null) {
            loadingViewDelegate.showContent(!allAttributes.isEmpty());
        }
    }

    @NonNull
    @Override
    public Event<ChangePlaygroundAttributesEventArgs> getChangePlaygroundAttributesEvent() {
        return _changePlaygroundAttributesEvent;
    }

    @NonNull
    @Override
    public Event<IdEventArgs> getViewPlaygroundAttributesEvent() {
        return _viewPlaygroundAttributesEvent;
    }

    @Override
    public void revertChangedAttributes() {
        final val attributesAdapter = getAttributesAdapter();
        if (attributesAdapter != null) {
            final val currentAttributeList = attributesAdapter.getAttributeItems();
            final val playgroundAttributes = getPlaygroundAttributes();
            for (final AttributeItem item : currentAttributeList) {
                final boolean isOff = !contains(playgroundAttributes, item);
                item.setOff(isOff);
            }
            attributesAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        riseViewAttributesEvent();
    }

    @CallSuper
    @Override
    protected void onInjectMembers() {
        super.onInjectMembers();

        getManagerSubscreenComponent().inject(this);

        final val presenter = getPresenter();
        if (presenter != null) {
            presenter.bindScreen(this);
        }
    }

    @Override
    @CallSuper
    protected void onPlaygroundIdChanged() {
        super.onPlaygroundIdChanged();

        if (isResumed()) {
            riseViewAttributesEvent();
        }
    }

    @Named(PresenterNames.ATTRIBUTE_EDITOR)
    @Inject
    @Nullable
    @Getter(AccessLevel.PROTECTED)
    /*package-private*/ Presenter<PlaygroundAttributesEditorScreen> _presenter;

    @NonNull
    private final ManagedEvent<ChangePlaygroundAttributesEventArgs>
        _changePlaygroundAttributesEvent = Events.createEvent();

    @NonNull
    private final ManagedEvent<IdEventArgs> _viewPlaygroundAttributesEvent = Events.createEvent();

    @NonNull
    @Getter
    @Setter
    private List<Attribute> _allAttributes = new ArrayList<>();

    @Getter(AccessLevel.PROTECTED)
    @Nullable
    private AttributesListAdapter _attributesAdapter;

    @Getter(AccessLevel.PROTECTED)
    @Nullable
    private LinearLayoutManager _attributesLayoutManager;

    @Nullable
    private RecyclerView _attributesListView;

    @Nullable
    private View _attributesLoadingErrorView;

    @Nullable
    private ProgressBar _attributesLoadingView;

    @Getter(AccessLevel.PROTECTED)
    @Nullable
    private LoadingViewDelegate _attributesLoadingViewDelegate;

    @Nullable
    private View _attributesNoContentView;

    @NonNull
    @Getter
    @Setter
    private List<Attribute> _playgroundAttributes = new ArrayList<>();

    private boolean contains(
        @NonNull final List<Attribute> attributes, @NonNull final Attribute attribute) {

        boolean isContains = false;
        for (final Attribute attr : attributes) {
            if (attr.getId() == attribute.getId()) {
                isContains = true;
                break;
            }
        }

        return isContains;
    }

    @NonNull
    private List<AttributeItem> createAttributeItems(
        @NonNull final List<Attribute> allAttributes,
        @NonNull final List<Attribute> playgroundAttributes) {

        final val attributeItems = new ArrayList<AttributeItem>();

        for (final Attribute attribute : allAttributes) {
            final val attr = new AttributeItem(attribute.getId(), attribute.getName());
            if (contains(playgroundAttributes, attribute)) {
                attr.setOff(false);
            } else {
                attr.setOff(true);
            }
            attributeItems.add(attr);
        }

        return attributeItems;
    }

    private void initAttributesListAdapter() {
        if (_attributesListView != null) {
            final val context = getContext();
            final val resources = context.getResources();

            _attributesLayoutManager = new LinearLayoutManager(context);
            _attributesAdapter = new AttributesListAdapter();

            final int spacing = resources.getDimensionPixelOffset(R.dimen.grid_large_spacing);

            final val spacingDecorator = SpacingItemDecoration
                .builder()
                .setSpacing(spacing)
                .collapseBorders()
                .enableEdges()
                .build();

            _attributesListView.addItemDecoration(spacingDecorator);
            _attributesListView.setLayoutManager(_attributesLayoutManager);
            _attributesListView.setAdapter(_attributesAdapter);
        }
    }

    private void riseViewAttributesEvent() {
        final val playgroundId = getPlaygroundId();
        if (playgroundId != null) {
            _viewPlaygroundAttributesEvent.rise(new IdEventArgs(playgroundId));
        } else {
            final val loadingViewDelegate = getAttributesLoadingViewDelegate();
            if (loadingViewDelegate != null) {
                loadingViewDelegate.showContent(false);
            }
        }
    }
}
