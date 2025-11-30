
-- 1) learning_enrollments
create table if not exists learning.learning_enrollments
(
    user_id          uuid        not null,
    course_id        uuid        not null,
    status           varchar     not null,
    source           varchar     not null,
    enrolled_at      timestamptz not null default now(),
    started_at       timestamptz null,
    completed_at     timestamptz null,
    revoked_at       timestamptz null,
    last_activity_at timestamptz null,
    constraint learning_enrollments_pk primary key (user_id, course_id),
    constraint learning_enrollments_status_chk check (status in ('IN_PROGRESS','COMPLETED','REVOKED')),
    constraint learning_enrollments_source_chk check (source in ('FREE','PAYMENT','INVITE','MANUAL'))
);

-- 2) learning_page_progress
create table if not exists learning.learning_page_progress
(
    user_id          uuid        not null,
    page_id          uuid        not null,
    status           varchar     not null,
    first_viewed_at  timestamptz null,
    last_activity_at timestamptz null,
    completed_at     timestamptz null,
    time_spent_sec   int         not null default 0,
    attempts_count   int         not null default 0,
    content_version  int         null,
    constraint learning_page_progress_pk primary key (user_id, page_id),
    constraint learning_page_progress_status_chk check (status in ('NOT_STARTED','IN_PROGRESS','COMPLETED')),
    constraint learning_page_progress_time_chk check (time_spent_sec >= 0),
    constraint learning_page_progress_attempts_chk check (attempts_count >= 0)
);

-- 3) learning_lesson_progress
create table if not exists learning.learning_lesson_progress
(
    user_id             uuid        not null,
    lesson_id           uuid        not null,
    progress_percentage int         not null,
    status              varchar     not null,
    computed_at         timestamptz not null default now(),
    content_version     int         null,
    constraint learning_lesson_progress_pk primary key (user_id, lesson_id),
    constraint learning_lesson_progress_progress_chk check (progress_percentage between 0 and 100),
    constraint learning_lesson_progress_status_chk check (status in ('NOT_STARTED','IN_PROGRESS','COMPLETED'))
);

-- 4) learning_course_progress
create table if not exists learning.learning_course_progress
(
    user_id             uuid        not null,
    course_id           uuid        not null,
    progress_percentage int         not null,
    status              varchar     not null,
    computed_at         timestamptz not null default now(),
    last_activity_at    timestamptz null,
    completed_at        timestamptz null,
    content_version     int         null,
    constraint learning_course_progress_pk primary key (user_id, course_id),
    constraint learning_course_progress_progress_chk check (progress_percentage between 0 and 100)
);

-- 5) learning_question_states
create table if not exists learning.learning_question_states
(
    user_id            uuid        not null,
    question_id        uuid        not null,
    is_solved          boolean     not null default false,
    solved_at          timestamptz null,
    last_submission_id uuid        null,
    last_status        varchar     null,
    last_updated_at    timestamptz not null default now(),
    constraint learning_question_states_pk primary key (user_id, question_id),
    constraint learning_question_states_status_chk
        check (last_status is null or last_status in ('ACCEPTED','REJECTED','PENDING'))
);

-- 6) learning_ratings
create table if not exists learning.learning_ratings
(
    user_id    uuid        not null,
    course_id  uuid        not null,
    value      int         not null,
    comment    text        null,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now(),
    constraint learning_ratings_pk primary key (user_id, course_id),
    constraint learning_ratings_value_chk check (value between 1 and 5)
);
