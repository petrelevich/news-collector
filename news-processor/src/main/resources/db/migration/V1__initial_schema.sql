create table news (
    news_link varchar(1000) not null primary key,
    news_date timestamp  not null,
    news_title varchar(1000) not null,
    news_text varchar(5000) not null,
    news_isin varchar(12),
    created_at timestamp not null,
    created_at_day date not null
);