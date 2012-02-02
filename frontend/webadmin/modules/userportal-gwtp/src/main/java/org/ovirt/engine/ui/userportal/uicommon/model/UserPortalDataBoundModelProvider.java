package org.ovirt.engine.ui.userportal.uicommon.model;

import java.util.List;

import org.ovirt.engine.ui.common.gin.BaseClientGinjector;
import org.ovirt.engine.ui.common.uicommon.model.DataBoundTabModelProvider;
import org.ovirt.engine.ui.uicommonweb.models.SearchableListModel;
import org.ovirt.engine.ui.userportal.uicommon.model.UserPortalModelInitEvent.UserPortalModelInitHandler;

/**
 * A {@link DataBoundTabModelProvider} that creates the UiCommon model instance directly, instead of accessing this
 * instance through CommonModel.
 * <p>
 * Suitable for use in situations where UiCommon models don't have a governing top-level CommonModel that creates them.
 *
 * @param <T>
 *            List model item type.
 * @param <M>
 *            List model type.
 */
public abstract class UserPortalDataBoundModelProvider<T, M extends SearchableListModel> extends DataBoundTabModelProvider<T, M> implements UserPortalModelInitHandler {

    public interface DataChangeListener<T> {

        void onDataChange(List<T> items);

    }

    private M model;
    private DataChangeListener<T> dataChangeListener;

    private List<T> selectedItems;

    public UserPortalDataBoundModelProvider(BaseClientGinjector ginjector) {
        super(ginjector);
        ginjector.getEventBus().addHandler(UserPortalModelInitEvent.getType(), this);
    }

    @Override
    protected boolean isModelReady() {
        return getModel() != null;
    }

    @Override
    public M getModel() {
        return model;
    }

    @Override
    public void onUserPortalModelInit(UserPortalModelInitEvent event) {
        this.model = createModel();
    }

    /**
     * Creates the model instance.
     */
    protected abstract M createModel();

    public void setDataChangeListener(DataChangeListener<T> changeListener) {
        this.dataChangeListener = changeListener;
    }

    @Override
    protected void updateDataProvider(List<T> items) {
        super.updateDataProvider(items);

        // Retain item selection within the model
        if (selectedItems != null) {
            super.setSelectedItems(selectedItems);
        }

        if (dataChangeListener != null) {
            dataChangeListener.onDataChange(items);
        }
    }

    @Override
    public void setSelectedItems(List<T> items) {
        super.setSelectedItems(items);

        // Remember current item selection
        if (rememberModelItemSelection()) {
            this.selectedItems = items;
        }
    }

    /**
     * @return {@code true} to remember item selection of the model and retain it upon data updates, {@code false}
     *         otherwise.
     */
    protected boolean rememberModelItemSelection() {
        return true;
    }

}
