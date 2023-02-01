package com.demo;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

public class NewsAggregatorApp extends JFrame {
    private final JList<NewsItem> newsList;
    private JButton showDetailsButton;
    private NewsItem selectedNewsItem;

    private static final String API_KEY = "0df928e98b4b44779a30e13dc4d975d1";
    private static final String API_URL = "https://newsapi.org/v2/top-headlines?country=gr&apiKey=" + API_KEY;

    public NewsAggregatorApp() throws IOException {
        setTitle("News Aggregator App");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout());

        // Fetch news items from a web service and store them in a list
        List<NewsItem> newsItems = fetchNewsItems();

        newsList = new JList<>(newsItems.toArray(new NewsItem[0]));
        newsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        newsList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    selectedNewsItem = newsList.getSelectedValue();
                    String details = selectedNewsItem.title() + "\n\n" + selectedNewsItem.description();
                    JOptionPane.showMessageDialog(NewsAggregatorApp.this, details, "News Details",
                        JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });
        JScrollPane newsListScrollPane = new JScrollPane(newsList);
        newsListScrollPane.setPreferredSize(new Dimension(400, 300));
        mainPanel.add(newsListScrollPane, BorderLayout.CENTER);

        showDetailsButton = new JButton("Show Details");
        showDetailsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectedNewsItem = newsList.getSelectedValue();
                if (selectedNewsItem != null) {
                    String details = selectedNewsItem.title() + "\n\n" + selectedNewsItem.description();
                    JOptionPane.showMessageDialog(NewsAggregatorApp.this, details, "News Details",
                        JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });
        mainPanel.add(showDetailsButton, BorderLayout.SOUTH);

        add(mainPanel);
        pack();
        setLocationRelativeTo(null);
    }

    private List<NewsItem> fetchNewsItems() throws IOException {
        List<NewsItem> newsItems = new ArrayList<>();

        HttpClient client = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet(API_URL);

        HttpResponse response = client.execute(request);
        String json = EntityUtils.toString(response.getEntity());

        JSONObject root = new JSONObject(json);
        JSONArray articles = root.getJSONArray("articles");

        for (int i = 0; i < articles.length(); i++) {
            JSONObject jsonObject = articles.getJSONObject(i);

            Object description1 = jsonObject.get("description");
            String description = "";
            if (description1 instanceof String) {
                description = jsonObject.getString("description");
            }

            String title = jsonObject.getString("title");
            if (!Objects.isNull(description) && !Objects.isNull(title)) {
                NewsItem newsItem = new NewsItem(title, description);
                newsItems.add(newsItem);
            }
        }

        return newsItems;
    }

    private record NewsItem(String title, String description) {

        @Override
        public String toString() {
            return title;
        }
    }
}