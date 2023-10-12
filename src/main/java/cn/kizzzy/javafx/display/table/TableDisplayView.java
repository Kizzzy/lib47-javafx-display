package cn.kizzzy.javafx.display.table;

import cn.kizzzy.data.TableFile;
import cn.kizzzy.helper.StringHelper;
import cn.kizzzy.javafx.JavafxControlParameter;
import cn.kizzzy.javafx.JavafxView;
import cn.kizzzy.javafx.control.LabeledTextField;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.Arrays;
import java.util.Comparator;
import java.util.ResourceBundle;

abstract class TableDisplayViewBase extends JavafxView {
    
    @FXML
    protected LabeledTextField fileNameTbf;
    
    @FXML
    protected CheckBox filterToggle;
    
    @FXML
    protected LabeledTextField filterColumn;
    
    @FXML
    protected LabeledTextField filterString;
    
    @FXML
    protected Button filterButton;
    
    @FXML
    protected TableView<String[]> tbv;
    
    @FXML
    protected Label info;
}

@JavafxControlParameter(fxml = "/fxml/display/table_view.fxml")
public class TableDisplayView extends TableDisplayViewBase implements Initializable {
    
    protected static final Logger logger = LoggerFactory.getLogger(TableDisplayView.class);
    
    protected FilteredList<String[]> filteredList;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tbv.getSelectionModel().getSelectedItems().addListener((ListChangeListener<String[]>) c -> {
            for (String[] array : c.getList()) {
                info.setText(Arrays.toString(array));
                break;
            }
            info.setMinHeight(48);
            info.setWrapText(true);
        });
        
        filterButton.setOnAction(this::doFilter);
    }
    
    public void show(TableArg args) {
        TableFile<String[]> pkgTxtFile = args.tableFile;
        
        tbv.getColumns().clear();
        
        String[] temp = pkgTxtFile.dataList.get(0);
        for (int j = 0; j < temp.length; ++j) {
            final int k = j;
            TableColumn<String[], String> column = new TableColumn<>("" + j);
            column.setCellValueFactory(cdf -> {
                if (k < cdf.getValue().length) {
                    return new SimpleStringProperty(cdf.getValue()[k]);
                } else {
                    return new SimpleStringProperty("");
                }
            });
            column.setComparator(Comparator.comparing(String::length).thenComparing(String::compareTo));
            column.setMinWidth(96);
            column.setMaxWidth(168);
            
            tbv.getColumns().add(column);
        }
        
        filteredList = new FilteredList<>(FXCollections.observableArrayList(pkgTxtFile.dataList));
        SortedList<String[]> sortedList = new SortedList<>(filteredList);
        sortedList.comparatorProperty().bind(tbv.comparatorProperty());
        tbv.setItems(sortedList);
    }
    
    @FXML
    protected void doFilter(ActionEvent event) {
        if (!filterToggle.isSelected()) {
            filteredList.setPredicate(null);
            return;
        }
        
        if (StringHelper.isNotNullAndEmpty(filterColumn.getContent()) &&
            StringHelper.isNotNullAndEmpty(filterString.getContent())) {
            try {
                final int column = Integer.parseInt(filterColumn.getContent());
                final String key = filterString.getContent();
                filteredList.setPredicate(
                    strings -> {
                        try {
                            String value = strings[column];
                            return value != null && value.contains(key);
                        } catch (Exception e) {
                            return false;
                        }
                    });
            } catch (Exception e) {
                logger.error("filter error", e);
            }
        }
    }
}
