create users (
    username_str varchar(256) primary key not null,
    password_str varchar(256) not null
);

create reviews (
    review_id_num serial,
    review_text_str text,
    review_cat_quiet_rating_num numeric
);
