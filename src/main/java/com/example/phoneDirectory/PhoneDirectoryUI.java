package com.example.phoneDirectory;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.servlet.annotation.WebServlet;

import com.example.domain.Phone;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.annotations.Widgetset;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.FileResource;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.Table.ColumnGenerator;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Reindeer;
import com.vaadin.ui.themes.ValoTheme;

@SuppressWarnings("serial")
@Theme("mytheme")
@Widgetset("com.example.phoneDirectory.MyAppWidgetset")
public class PhoneDirectoryUI extends UI {
	
	private Properties prop = new Properties();
	File file = null;
	
	@Override
	protected void init(VaadinRequest vaadinRequest) {
		final VerticalLayout layout = new VerticalLayout();
		layout.setMargin(true);
		layout.setSpacing(true);
		layout.setSizeFull();
		setContent(layout);

		Label label = new Label("PhoneDirectory");
		HorizontalLayout header = new HorizontalLayout(label);
		header.setWidth("100%");
		label.setStyleName("center");
		header.setComponentAlignment(label, Alignment.TOP_CENTER);
		layout.addComponent(header);

		BeanItemContainer<Phone> container = new BeanItemContainer<Phone>(
				Phone.class, getPhoneList());

		Table table = new Table(null, container);
		table.setVisibleColumns("personName", "number");
		table.setColumnHeaders("Person", "Number");
		table.setPageLength(table.size());
		table.setSelectable(true);
		table.addStyleName(Reindeer.TABSHEET_MINIMAL);
		table.addGeneratedColumn("Delete", new ColumnGenerator() {

			@Override
			public Object generateCell(Table source, Object itemId,
					Object columnId) {

				Button deleteBtn = new Button();
				deleteBtn.setIcon(FontAwesome.REMOVE);
				// deleteBtn.addStyleName("quiet");
				deleteBtn.addStyleName(ValoTheme.BUTTON_QUIET);
				// deleteBtn.addStyleName(Reindeer.BUTTON_DEFAULT);
				deleteBtn.addClickListener(new Button.ClickListener() {

					@Override
					public void buttonClick(ClickEvent event) {
						// remove from table
						table.removeItem(itemId);
						table.setPageLength(table.size());
						
						// remove from file
						Properties prop = new Properties();
						
						try (FileReader reader = new FileReader(file);) {
							prop.load(reader);
							
							try (FileWriter writer = new FileWriter(file);) {
								Phone phone = (Phone) itemId;
								prop.remove(phone.getPersonName());
								prop.store(writer, null);
							}
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				});
				return deleteBtn;
			}
		});
		table.setColumnAlignment("Delete", Align.CENTER);

		layout.addComponent(table);
		layout.setComponentAlignment(table, Alignment.TOP_CENTER);
		layout.setExpandRatio(header, 0);
		layout.setExpandRatio(table, 1f);
	}

	private List<Phone> getPhoneList() {
		List<Phone> phones = new ArrayList<>();
		String basepath = VaadinService.getCurrent().getBaseDirectory()
				.getAbsolutePath();
		FileResource resource = new FileResource(new File(basepath
				+ "/resources/numberList.properties"));
		file = resource.getSourceFile();

		try (FileReader reader = new FileReader(file);) {
			prop.load(reader);

			for (Object personName : prop.keySet())
				phones.add(new Phone(personName.toString(), prop
						.get(personName).toString()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return phones;
	}

	@WebServlet(urlPatterns = "/*", name = "PhoneDirectoryUIServlet", asyncSupported = true)
	@VaadinServletConfiguration(ui = PhoneDirectoryUI.class, productionMode = false)
	public static class PhoneDirectoryUIServlet extends VaadinServlet {
	}
}
