--
-- PostgreSQL database dump
--

\restrict xGitutt6N0Ozmb546wxZFs9e9BOhLSlpS9UOWFrVde2GTjjuKf4yvGMKCMiiLjc

-- Dumped from database version 16.11
-- Dumped by pg_dump version 16.11

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: chat_sender_enum; Type: TYPE; Schema: public; Owner: postgres
--

CREATE TYPE public.chat_sender_enum AS ENUM (
    'USER',
    'BOT'
);


ALTER TYPE public.chat_sender_enum OWNER TO postgres;

--
-- Name: chat_status_enum; Type: TYPE; Schema: public; Owner: postgres
--

CREATE TYPE public.chat_status_enum AS ENUM (
    'OPEN',
    'CLOSED'
);


ALTER TYPE public.chat_status_enum OWNER TO postgres;

--
-- Name: device_platform_enum; Type: TYPE; Schema: public; Owner: postgres
--

CREATE TYPE public.device_platform_enum AS ENUM (
    'ANDROID',
    'IOS'
);


ALTER TYPE public.device_platform_enum OWNER TO postgres;

--
-- Name: issue_source_enum; Type: TYPE; Schema: public; Owner: postgres
--

CREATE TYPE public.issue_source_enum AS ENUM (
    'MANUAL',
    'AI'
);


ALTER TYPE public.issue_source_enum OWNER TO postgres;

--
-- Name: issue_status_enum; Type: TYPE; Schema: public; Owner: postgres
--

CREATE TYPE public.issue_status_enum AS ENUM (
    'OPEN',
    'IN_REVIEW',
    'CONVERTED_TO_WO',
    'CLOSED'
);


ALTER TYPE public.issue_status_enum OWNER TO postgres;

--
-- Name: issue_type_enum; Type: TYPE; Schema: public; Owner: postgres
--

CREATE TYPE public.issue_type_enum AS ENUM (
    'MECHANICAL',
    'THERMAL',
    'ELECTRICAL',
    'PROCESS',
    'SAFETY'
);


ALTER TYPE public.issue_type_enum OWNER TO postgres;

--
-- Name: machine_criticality_enum; Type: TYPE; Schema: public; Owner: postgres
--

CREATE TYPE public.machine_criticality_enum AS ENUM (
    'LOW',
    'MEDIUM',
    'HIGH'
);


ALTER TYPE public.machine_criticality_enum OWNER TO postgres;

--
-- Name: machine_status_enum; Type: TYPE; Schema: public; Owner: postgres
--

CREATE TYPE public.machine_status_enum AS ENUM (
    'HEALTHY',
    'WARNING',
    'CRITICAL',
    'OFFLINE'
);


ALTER TYPE public.machine_status_enum OWNER TO postgres;

--
-- Name: notif_type_enum; Type: TYPE; Schema: public; Owner: postgres
--

CREATE TYPE public.notif_type_enum AS ENUM (
    'PREDICTED_FAILURE',
    'SENSOR_ALERT',
    'NEW_WORK_ORDER',
    'WO_STATUS_CHANGED'
);


ALTER TYPE public.notif_type_enum OWNER TO postgres;

--
-- Name: org_status_enum; Type: TYPE; Schema: public; Owner: postgres
--

CREATE TYPE public.org_status_enum AS ENUM (
    'ACTIVE',
    'SUSPENDED'
);


ALTER TYPE public.org_status_enum OWNER TO postgres;

--
-- Name: prediction_severity_enum; Type: TYPE; Schema: public; Owner: postgres
--

CREATE TYPE public.prediction_severity_enum AS ENUM (
    'LOW',
    'MEDIUM',
    'HIGH',
    'CRITICAL'
);


ALTER TYPE public.prediction_severity_enum OWNER TO postgres;

--
-- Name: reading_quality_enum; Type: TYPE; Schema: public; Owner: postgres
--

CREATE TYPE public.reading_quality_enum AS ENUM (
    'VALID',
    'INVALID'
);


ALTER TYPE public.reading_quality_enum OWNER TO postgres;

--
-- Name: request_status_enum; Type: TYPE; Schema: public; Owner: postgres
--

CREATE TYPE public.request_status_enum AS ENUM (
    'PENDING',
    'APPROVED',
    'REJECTED'
);


ALTER TYPE public.request_status_enum OWNER TO postgres;

--
-- Name: requested_service_enum; Type: TYPE; Schema: public; Owner: postgres
--

CREATE TYPE public.requested_service_enum AS ENUM (
    'MONITORING',
    'PREDICTIVE',
    'BOTH'
);


ALTER TYPE public.requested_service_enum OWNER TO postgres;

--
-- Name: sensor_status_enum; Type: TYPE; Schema: public; Owner: postgres
--

CREATE TYPE public.sensor_status_enum AS ENUM (
    'ONLINE',
    'OFFLINE'
);


ALTER TYPE public.sensor_status_enum OWNER TO postgres;

--
-- Name: user_role_enum; Type: TYPE; Schema: public; Owner: postgres
--

CREATE TYPE public.user_role_enum AS ENUM (
    'SYSTEM_ADMIN',
    'COMPANY_ADMIN',
    'ENGINEER',
    'TECHNICIAN'
);


ALTER TYPE public.user_role_enum OWNER TO postgres;

--
-- Name: user_status_enum; Type: TYPE; Schema: public; Owner: postgres
--

CREATE TYPE public.user_status_enum AS ENUM (
    'INVITED',
    'ACTIVE',
    'DISABLED'
);


ALTER TYPE public.user_status_enum OWNER TO postgres;

--
-- Name: wo_priority_enum; Type: TYPE; Schema: public; Owner: postgres
--

CREATE TYPE public.wo_priority_enum AS ENUM (
    'LOW',
    'MEDIUM',
    'HIGH',
    'CRITICAL'
);


ALTER TYPE public.wo_priority_enum OWNER TO postgres;

--
-- Name: wo_status_enum; Type: TYPE; Schema: public; Owner: postgres
--

CREATE TYPE public.wo_status_enum AS ENUM (
    'OPEN',
    'ASSIGNED',
    'IN_PROGRESS',
    'COMPLETED',
    'CLOSED'
);


ALTER TYPE public.wo_status_enum OWNER TO postgres;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: ai_model_info; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.ai_model_info (
    id bigint NOT NULL,
    organization_id bigint NOT NULL,
    model_name character varying(120) NOT NULL,
    version character varying(50) NOT NULL,
    features_used_json jsonb,
    last_training_date date,
    notes character varying(255)
);


ALTER TABLE public.ai_model_info OWNER TO postgres;

--
-- Name: ai_model_info_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.ai_model_info_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.ai_model_info_id_seq OWNER TO postgres;

--
-- Name: ai_model_info_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.ai_model_info_id_seq OWNED BY public.ai_model_info.id;


--
-- Name: app_user; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.app_user (
    id bigint NOT NULL,
    organization_id bigint,
    full_name character varying(120) NOT NULL,
    email character varying(120) NOT NULL,
    phone character varying(30),
    role character varying(255) NOT NULL,
    status character varying(255) DEFAULT 'INVITED'::public.user_status_enum NOT NULL,
    password_hash character varying(255),
    created_at timestamp without time zone DEFAULT now() NOT NULL,
    avatar text,
    reset_otp character varying(6),
    reset_otp_expires_at timestamp without time zone
);


ALTER TABLE public.app_user OWNER TO postgres;

--
-- Name: app_user_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.app_user_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.app_user_id_seq OWNER TO postgres;

--
-- Name: app_user_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.app_user_id_seq OWNED BY public.app_user.id;


--
-- Name: asset_type; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.asset_type (
    id bigint NOT NULL,
    organization_id bigint NOT NULL,
    name character varying(100) NOT NULL,
    description character varying(255),
    industry character varying(100),
    created_at timestamp without time zone DEFAULT now() NOT NULL
);


ALTER TABLE public.asset_type OWNER TO postgres;

--
-- Name: asset_type_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.asset_type_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.asset_type_id_seq OWNER TO postgres;

--
-- Name: asset_type_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.asset_type_id_seq OWNED BY public.asset_type.id;


--
-- Name: chat_conversation; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.chat_conversation (
    id bigint NOT NULL,
    organization_id bigint NOT NULL,
    user_id bigint NOT NULL,
    context_machine_id bigint,
    context_work_order_id bigint,
    status public.chat_status_enum DEFAULT 'OPEN'::public.chat_status_enum NOT NULL,
    created_at timestamp without time zone DEFAULT now() NOT NULL,
    last_message_at timestamp without time zone
);


ALTER TABLE public.chat_conversation OWNER TO postgres;

--
-- Name: chat_conversation_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.chat_conversation_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.chat_conversation_id_seq OWNER TO postgres;

--
-- Name: chat_conversation_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.chat_conversation_id_seq OWNED BY public.chat_conversation.id;


--
-- Name: chat_message; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.chat_message (
    id bigint NOT NULL,
    conversation_id bigint NOT NULL,
    sender public.chat_sender_enum NOT NULL,
    message_text text NOT NULL,
    created_at timestamp without time zone DEFAULT now() NOT NULL
);


ALTER TABLE public.chat_message OWNER TO postgres;

--
-- Name: chat_message_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.chat_message_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.chat_message_id_seq OWNER TO postgres;

--
-- Name: chat_message_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.chat_message_id_seq OWNED BY public.chat_message.id;


--
-- Name: issue; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.issue (
    id bigint NOT NULL,
    organization_id bigint NOT NULL,
    machine_id bigint NOT NULL,
    created_by_user_id bigint NOT NULL,
    source character varying(255) NOT NULL,
    prediction_id bigint,
    summary character varying(200) NOT NULL,
    details text,
    severity character varying(255),
    status character varying(255) DEFAULT 'OPEN'::public.issue_status_enum NOT NULL,
    created_at timestamp without time zone DEFAULT now() NOT NULL
);


ALTER TABLE public.issue OWNER TO postgres;

--
-- Name: issue_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.issue_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.issue_id_seq OWNER TO postgres;

--
-- Name: issue_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.issue_id_seq OWNED BY public.issue.id;


--
-- Name: machine; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.machine (
    id bigint NOT NULL,
    organization_id bigint NOT NULL,
    asset_type_id bigint,
    name character varying(120) NOT NULL,
    machine_type character varying(100),
    serial_number character varying(100),
    location character varying(150),
    criticality character varying(255) DEFAULT 'MEDIUM'::public.machine_criticality_enum NOT NULL,
    status character varying(255) DEFAULT 'HEALTHY'::public.machine_status_enum NOT NULL,
    installation_date date,
    operating_hours numeric(10,2),
    operating_cycles numeric(10,2),
    created_at timestamp without time zone DEFAULT now() NOT NULL,
    asset_id character varying(50) NOT NULL
);


ALTER TABLE public.machine OWNER TO postgres;

--
-- Name: machine_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.machine_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.machine_id_seq OWNER TO postgres;

--
-- Name: machine_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.machine_id_seq OWNED BY public.machine.id;


--
-- Name: notification; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.notification (
    id bigint NOT NULL,
    organization_id bigint NOT NULL,
    recipient_user_id bigint NOT NULL,
    type character varying(255) NOT NULL,
    severity character varying(255),
    title character varying(160) NOT NULL,
    message character varying(500) NOT NULL,
    is_read boolean DEFAULT false NOT NULL,
    created_at timestamp without time zone DEFAULT now() NOT NULL,
    machine_id bigint,
    work_order_id bigint,
    prediction_id bigint,
    acknowledged boolean DEFAULT false NOT NULL,
    acknowledged_by character varying(120),
    acknowledged_at timestamp without time zone
);


ALTER TABLE public.notification OWNER TO postgres;

--
-- Name: notification_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.notification_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.notification_id_seq OWNER TO postgres;

--
-- Name: notification_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.notification_id_seq OWNED BY public.notification.id;


--
-- Name: organization; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.organization (
    id bigint NOT NULL,
    company_name character varying(150) NOT NULL,
    industry character varying(100),
    contact_person_name character varying(120),
    email character varying(120),
    phone character varying(30),
    requested_service character varying(255),
    status character varying(255) DEFAULT 'ACTIVE'::public.org_status_enum NOT NULL,
    timezone character varying(50),
    logo_url character varying(255),
    onboarding_completed boolean DEFAULT false NOT NULL,
    created_at timestamp without time zone DEFAULT now() NOT NULL,
    reviewed_at timestamp without time zone,
    code character varying(20)
);


ALTER TABLE public.organization OWNER TO postgres;

--
-- Name: organization_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.organization_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.organization_id_seq OWNER TO postgres;

--
-- Name: organization_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.organization_id_seq OWNED BY public.organization.id;


--
-- Name: organization_request; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.organization_request (
    id bigint NOT NULL,
    company_name character varying(150) NOT NULL,
    industry character varying(100),
    contact_person_name character varying(120) NOT NULL,
    email character varying(120) NOT NULL,
    phone character varying(30),
    requested_service character varying(255) NOT NULL,
    status character varying(255) DEFAULT 'PENDING'::public.request_status_enum NOT NULL,
    created_at timestamp without time zone DEFAULT now() NOT NULL,
    reviewed_at timestamp without time zone
);


ALTER TABLE public.organization_request OWNER TO postgres;

--
-- Name: organization_request_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.organization_request_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.organization_request_id_seq OWNER TO postgres;

--
-- Name: organization_request_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.organization_request_id_seq OWNED BY public.organization_request.id;


--
-- Name: prediction; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.prediction (
    id bigint NOT NULL,
    organization_id bigint NOT NULL,
    machine_id bigint NOT NULL,
    failure_probability numeric(5,2),
    suggested_issue_type character varying(255),
    severity character varying(255),
    model_version character varying(50),
    predicted_at timestamp without time zone DEFAULT now() NOT NULL,
    explanation text,
    rul_cycles numeric(10,2),
    ttf_hours numeric(10,2)
);


ALTER TABLE public.prediction OWNER TO postgres;

--
-- Name: prediction_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.prediction_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.prediction_id_seq OWNER TO postgres;

--
-- Name: prediction_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.prediction_id_seq OWNED BY public.prediction.id;


--
-- Name: sensor; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.sensor (
    id bigint NOT NULL,
    organization_id bigint NOT NULL,
    machine_id bigint NOT NULL,
    sensor_type_id bigint NOT NULL,
    external_ref character varying(100),
    status public.sensor_status_enum DEFAULT 'ONLINE'::public.sensor_status_enum NOT NULL,
    current_value numeric(10,2),
    last_reading_at timestamp without time zone,
    created_at timestamp without time zone DEFAULT now() NOT NULL
);


ALTER TABLE public.sensor OWNER TO postgres;

--
-- Name: sensor_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.sensor_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.sensor_id_seq OWNER TO postgres;

--
-- Name: sensor_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.sensor_id_seq OWNED BY public.sensor.id;


--
-- Name: sensor_reading; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.sensor_reading (
    id bigint NOT NULL,
    sensor_id bigint NOT NULL,
    value double precision NOT NULL,
    reading_time timestamp without time zone NOT NULL,
    quality public.reading_quality_enum DEFAULT 'VALID'::public.reading_quality_enum NOT NULL,
    ingested_at timestamp without time zone DEFAULT now() NOT NULL
);


ALTER TABLE public.sensor_reading OWNER TO postgres;

--
-- Name: sensor_reading_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.sensor_reading_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.sensor_reading_id_seq OWNER TO postgres;

--
-- Name: sensor_reading_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.sensor_reading_id_seq OWNED BY public.sensor_reading.id;


--
-- Name: sensor_type; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.sensor_type (
    id bigint NOT NULL,
    organization_id bigint NOT NULL,
    name character varying(100) NOT NULL,
    unit character varying(30) NOT NULL,
    default_warning_threshold numeric(10,2),
    default_critical_threshold numeric(10,2),
    created_at timestamp without time zone DEFAULT now() NOT NULL
);


ALTER TABLE public.sensor_type OWNER TO postgres;

--
-- Name: sensor_type_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.sensor_type_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.sensor_type_id_seq OWNER TO postgres;

--
-- Name: sensor_type_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.sensor_type_id_seq OWNED BY public.sensor_type.id;


--
-- Name: test_machine; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.test_machine (
    id bigint NOT NULL,
    name character varying(255)
);


ALTER TABLE public.test_machine OWNER TO postgres;

--
-- Name: test_machine_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

ALTER TABLE public.test_machine ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public.test_machine_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: test_table; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.test_table (
    id integer NOT NULL,
    name text
);


ALTER TABLE public.test_table OWNER TO postgres;

--
-- Name: test_table_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.test_table_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.test_table_id_seq OWNER TO postgres;

--
-- Name: test_table_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.test_table_id_seq OWNED BY public.test_table.id;


--
-- Name: threshold; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.threshold (
    id bigint NOT NULL,
    organization_id bigint NOT NULL,
    asset_type_id bigint NOT NULL,
    sensor_type_id bigint NOT NULL,
    warning_value numeric(10,2) NOT NULL,
    critical_value numeric(10,2) NOT NULL,
    updated_by_user_id bigint NOT NULL,
    updated_at timestamp without time zone DEFAULT now() NOT NULL
);


ALTER TABLE public.threshold OWNER TO postgres;

--
-- Name: threshold_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.threshold_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.threshold_id_seq OWNER TO postgres;

--
-- Name: threshold_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.threshold_id_seq OWNED BY public.threshold.id;


--
-- Name: user_asset_assignment; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.user_asset_assignment (
    id bigint NOT NULL,
    organization_id bigint NOT NULL,
    user_id bigint NOT NULL,
    machine_id bigint NOT NULL,
    created_at timestamp without time zone DEFAULT now() NOT NULL
);


ALTER TABLE public.user_asset_assignment OWNER TO postgres;

--
-- Name: user_asset_assignment_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.user_asset_assignment_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.user_asset_assignment_id_seq OWNER TO postgres;

--
-- Name: user_asset_assignment_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.user_asset_assignment_id_seq OWNED BY public.user_asset_assignment.id;


--
-- Name: user_device_token; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.user_device_token (
    id bigint NOT NULL,
    user_id bigint NOT NULL,
    device_platform public.device_platform_enum NOT NULL,
    device_token character varying(255) NOT NULL,
    last_seen_at timestamp without time zone
);


ALTER TABLE public.user_device_token OWNER TO postgres;

--
-- Name: user_device_token_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.user_device_token_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.user_device_token_id_seq OWNER TO postgres;

--
-- Name: user_device_token_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.user_device_token_id_seq OWNED BY public.user_device_token.id;


--
-- Name: work_order; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.work_order (
    id bigint NOT NULL,
    organization_id bigint NOT NULL,
    machine_id bigint NOT NULL,
    issue_id bigint,
    created_by_user_id bigint NOT NULL,
    assigned_to_user_id bigint,
    priority character varying(255) DEFAULT 'MEDIUM'::public.wo_priority_enum NOT NULL,
    status character varying(255) DEFAULT 'OPEN'::public.wo_status_enum NOT NULL,
    due_date date,
    title character varying(150) NOT NULL,
    description text,
    ai_suggested boolean DEFAULT false NOT NULL,
    created_at timestamp without time zone DEFAULT now() NOT NULL,
    closed_at timestamp without time zone
);


ALTER TABLE public.work_order OWNER TO postgres;

--
-- Name: work_order_completion; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.work_order_completion (
    id bigint NOT NULL,
    work_order_id bigint NOT NULL,
    completed_by_user_id bigint NOT NULL,
    action_taken text NOT NULL,
    root_cause character varying(150),
    time_spent_minutes integer NOT NULL,
    additional_notes text,
    completed_at timestamp without time zone DEFAULT now() NOT NULL
);


ALTER TABLE public.work_order_completion OWNER TO postgres;

--
-- Name: work_order_completion_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.work_order_completion_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.work_order_completion_id_seq OWNER TO postgres;

--
-- Name: work_order_completion_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.work_order_completion_id_seq OWNED BY public.work_order_completion.id;


--
-- Name: work_order_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.work_order_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.work_order_id_seq OWNER TO postgres;

--
-- Name: work_order_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.work_order_id_seq OWNED BY public.work_order.id;


--
-- Name: work_order_spare_part; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.work_order_spare_part (
    id bigint NOT NULL,
    completion_id bigint NOT NULL,
    part_name character varying(150) NOT NULL,
    quantity integer DEFAULT 1 NOT NULL
);


ALTER TABLE public.work_order_spare_part OWNER TO postgres;

--
-- Name: work_order_spare_part_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.work_order_spare_part_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.work_order_spare_part_id_seq OWNER TO postgres;

--
-- Name: work_order_spare_part_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.work_order_spare_part_id_seq OWNED BY public.work_order_spare_part.id;


--
-- Name: ai_model_info id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.ai_model_info ALTER COLUMN id SET DEFAULT nextval('public.ai_model_info_id_seq'::regclass);


--
-- Name: app_user id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.app_user ALTER COLUMN id SET DEFAULT nextval('public.app_user_id_seq'::regclass);


--
-- Name: asset_type id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.asset_type ALTER COLUMN id SET DEFAULT nextval('public.asset_type_id_seq'::regclass);


--
-- Name: chat_conversation id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.chat_conversation ALTER COLUMN id SET DEFAULT nextval('public.chat_conversation_id_seq'::regclass);


--
-- Name: chat_message id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.chat_message ALTER COLUMN id SET DEFAULT nextval('public.chat_message_id_seq'::regclass);


--
-- Name: issue id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.issue ALTER COLUMN id SET DEFAULT nextval('public.issue_id_seq'::regclass);


--
-- Name: machine id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.machine ALTER COLUMN id SET DEFAULT nextval('public.machine_id_seq'::regclass);


--
-- Name: notification id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.notification ALTER COLUMN id SET DEFAULT nextval('public.notification_id_seq'::regclass);


--
-- Name: organization id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.organization ALTER COLUMN id SET DEFAULT nextval('public.organization_id_seq'::regclass);


--
-- Name: organization_request id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.organization_request ALTER COLUMN id SET DEFAULT nextval('public.organization_request_id_seq'::regclass);


--
-- Name: prediction id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.prediction ALTER COLUMN id SET DEFAULT nextval('public.prediction_id_seq'::regclass);


--
-- Name: sensor id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.sensor ALTER COLUMN id SET DEFAULT nextval('public.sensor_id_seq'::regclass);


--
-- Name: sensor_reading id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.sensor_reading ALTER COLUMN id SET DEFAULT nextval('public.sensor_reading_id_seq'::regclass);


--
-- Name: sensor_type id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.sensor_type ALTER COLUMN id SET DEFAULT nextval('public.sensor_type_id_seq'::regclass);


--
-- Name: test_table id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.test_table ALTER COLUMN id SET DEFAULT nextval('public.test_table_id_seq'::regclass);


--
-- Name: threshold id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.threshold ALTER COLUMN id SET DEFAULT nextval('public.threshold_id_seq'::regclass);


--
-- Name: user_asset_assignment id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.user_asset_assignment ALTER COLUMN id SET DEFAULT nextval('public.user_asset_assignment_id_seq'::regclass);


--
-- Name: user_device_token id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.user_device_token ALTER COLUMN id SET DEFAULT nextval('public.user_device_token_id_seq'::regclass);


--
-- Name: work_order id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.work_order ALTER COLUMN id SET DEFAULT nextval('public.work_order_id_seq'::regclass);


--
-- Name: work_order_completion id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.work_order_completion ALTER COLUMN id SET DEFAULT nextval('public.work_order_completion_id_seq'::regclass);


--
-- Name: work_order_spare_part id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.work_order_spare_part ALTER COLUMN id SET DEFAULT nextval('public.work_order_spare_part_id_seq'::regclass);


--
-- Data for Name: ai_model_info; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.ai_model_info (id, organization_id, model_name, version, features_used_json, last_training_date, notes) FROM stdin;
\.


--
-- Data for Name: app_user; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.app_user (id, organization_id, full_name, email, phone, role, status, password_hash, created_at, avatar, reset_otp, reset_otp_expires_at) FROM stdin;
2	1	Tech User	tech@minimaxi.com	\N	TECHNICIAN	ACTIVE	\N	2026-04-21 03:01:17.75128	\N	\N	\N
4	1	Test User	test@minimaxi.com	+20 100 000 0000	TECHNICIAN	INVITED	\N	2026-04-22 14:34:42.982419	\N	\N	\N
5	1	Invited User	invited@minimaxi.com	\N	TECHNICIAN	INVITED	\N	2026-04-22 14:35:58.519236	\N	\N	\N
12	1	Invited User 2	invited2@minimaxi.com	\N	TECHNICIAN	INVITED	\N	2026-04-22 14:42:07.687528	\N	\N	\N
1	1	Updated Name	admin@minimaxi.com	+20 111 111 1111	ENGINEER	DISABLED	\N	2026-04-21 03:01:17.75128	data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNk+M9QDwADhgGAWjR9awAAAABJRU5ErkJggg==	\N	\N
3	4	Sara Ahmed	sara@futurefactory.com	01012345678	COMPANY_ADMIN	ACTIVE	$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy	2026-04-21 01:45:07.945763	\N	\N	\N
15	4	Test Admin	testadmin@minimaxi.com	\N	COMPANY_ADMIN	ACTIVE	$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy	2026-04-25 22:58:47.670078	\N	\N	\N
17	6	Admin	olafarhat579@gmail.com	01234567890	COMPANY_ADMIN	ACTIVE	$2a$10$D7RX6P4ifUPO1/eHJmR1yum6eUZCgIQfcnNi78iMfer9u/SyIAGPm	2026-04-28 08:20:13.021834	\N	\N	\N
16	5	Test Admin	newadmin@test.com	01234567890	COMPANY_ADMIN	ACTIVE	$2a$10$JrdhfBMjq5xtk8f3hx0gHOipfKYgjS8U4K.q4oYi66S4PdG/wF1ge	2026-04-25 20:01:17.787687	\N	329036	2026-04-30 03:03:32.934742
19	8	Salma Yasser	01030845657ss@gmail.com	01234567890	COMPANY_ADMIN	ACTIVE	$2a$10$lSttvuHKV1qj1pE5XtXQX.zMS4x2DMdj9aQBNjb1QMcDKJVP8Y1J6	2026-04-30 02:59:07.445163	\N	544178	2026-04-30 13:35:39.230034
27	16	Mo Yasser	moyassermostafa2007@gmail.com	01234567890	COMPANY_ADMIN	ACTIVE	$2a$10$IHI9DCEaoJFSjG528TD/yO/FJTHkY6SWYhbzdyS.67aOkV4qmsDny	2026-04-30 17:29:25.94802	\N	\N	\N
\.


--
-- Data for Name: asset_type; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.asset_type (id, organization_id, name, description, industry, created_at) FROM stdin;
1	1	Engine	Industrial engines	Factory	2026-04-23 04:25:53.944114
2	1	Pump	Hydraulic pumps	Factory	2026-04-23 04:25:53.944114
3	1	Compressor	Air compressors	Factory	2026-04-23 04:25:53.944114
\.


--
-- Data for Name: chat_conversation; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.chat_conversation (id, organization_id, user_id, context_machine_id, context_work_order_id, status, created_at, last_message_at) FROM stdin;
\.


--
-- Data for Name: chat_message; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.chat_message (id, conversation_id, sender, message_text, created_at) FROM stdin;
\.


--
-- Data for Name: issue; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.issue (id, organization_id, machine_id, created_by_user_id, source, prediction_id, summary, details, severity, status, created_at) FROM stdin;
\.


--
-- Data for Name: machine; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.machine (id, organization_id, asset_type_id, name, machine_type, serial_number, location, criticality, status, installation_date, operating_hours, operating_cycles, created_at, asset_id) FROM stdin;
1	1	\N	CNC Machine	CNC	\N	Factory A	MEDIUM	HEALTHY	\N	\N	\N	2026-04-21 00:45:53.784415	MCH-1
8	4	\N	Hydraulic Pump Beta	Pump	SN-002	Plant B	MEDIUM	HEALTHY	2021-06-20	\N	\N	2026-04-23 04:38:02.226817	MCH-102
10	4	\N	Motor Delta	Motor	SN-004	Plant C	LOW	HEALTHY	2023-02-01	\N	\N	2026-04-23 04:38:02.226817	MCH-104
11	4	\N	Turbine Epsilon	Turbine	SN-005	Plant B	HIGH	HEALTHY	2019-11-05	\N	\N	2026-04-23 04:38:02.226817	MCH-105
7	4	\N	CNC Machine Alpha	CNC	SN-001	Plant A	HIGH	WARNING	2022-01-15	\N	\N	2026-04-23 04:38:02.226817	MCH-101
9	4	\N	Air Compressor Gamma	Compressor	SN-003	Plant A	HIGH	CRITICAL	2020-03-10	\N	\N	2026-04-23 04:38:02.226817	MCH-103
\.


--
-- Data for Name: notification; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.notification (id, organization_id, recipient_user_id, type, severity, title, message, is_read, created_at, machine_id, work_order_id, prediction_id, acknowledged, acknowledged_by, acknowledged_at) FROM stdin;
2	4	3	PREDICTED_FAILURE	HIGH	Predicted failure detected	The system detected a high probability of machine failure.	t	2026-04-21 03:07:02.288385	1	\N	1	f	\N	\N
1	4	3	NEW_WORK_ORDER	MEDIUM	New work order assigned	A new work order has been assigned to you.	t	2026-04-21 04:07:02.288385	1	1	\N	t	Ahmed Mohamed	2026-04-22 15:01:15.76778
3	4	3	PREDICTED_FAILURE	CRITICAL	Critical failure predicted - Air Compressor	Air Compressor Gamma has 91% failure probability. Immediate action required.	f	2026-04-23 04:43:44.149674	9	\N	\N	f	\N	\N
4	4	3	PREDICTED_FAILURE	HIGH	High failure risk - CNC Machine	CNC Machine Alpha has 72.5% failure probability. Inspection recommended.	f	2026-04-23 03:43:44.149674	7	\N	\N	f	\N	\N
5	4	3	PREDICTED_FAILURE	MEDIUM	Maintenance needed - Turbine Epsilon	Turbine Epsilon temperature trending upward. Preventive maintenance suggested.	f	2026-04-23 02:43:44.149674	11	\N	\N	f	\N	\N
6	4	3	NEW_WORK_ORDER	HIGH	New work order assigned to you	Emergency maintenance work order created for Air Compressor Gamma.	f	2026-04-23 04:13:44.149674	9	\N	\N	f	\N	\N
7	4	3	WO_STATUS_CHANGED	MEDIUM	Work order status updated	Inspect CNC spindle bearing is now In Progress.	t	2026-04-23 01:43:44.149674	7	\N	\N	f	\N	\N
\.


--
-- Data for Name: organization; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.organization (id, company_name, industry, contact_person_name, email, phone, requested_service, status, timezone, logo_url, onboarding_completed, created_at, reviewed_at, code) FROM stdin;
1	MiniMaxi	\N	\N	\N	\N	\N	ACTIVE	\N	\N	f	2026-04-21 00:45:53.784415	\N	MM
2	Future Factory	Manufacturing	Sara Ahmed	sara@futurefactory.com	01012345678	BOTH	ACTIVE	\N	\N	f	2026-04-21 01:41:45.527593	\N	\N
3	Future Factory	Manufacturing	Sara Ahmed	sara@futurefactory.com	01012345678	BOTH	ACTIVE	\N	\N	f	2026-04-21 01:42:18.014078	\N	\N
4	Future Factory Updated	Smart Manufacturing	Sara Ahmed	sara@futurefactory.com	01012345678	BOTH	ACTIVE	Africa/Cairo	https://example.com/logo.png	t	2026-04-21 01:45:07.938547	\N	\N
5	Test Company	\N	Test Admin	newadmin@test.com	01234567890	BOTH	ACTIVE	\N	\N	f	2026-04-25 20:01:17.721093	\N	\N
6	Ola Farhat	\N	Admin	olafarhat579@gmail.com	01234567890	BOTH	ACTIVE	\N	\N	f	2026-04-28 08:20:12.818879	\N	\N
7	Ola	Manufacturing	Ola Farhat	olafarhat579@gmail.com	01234567890	BOTH	ACTIVE	\N	\N	f	2026-04-30 02:58:52.710375	\N	\N
8	salmaTesting	Manufacturing	Salma Yasser	01030845657ss@gmail.com	01234567890	BOTH	ACTIVE	\N	\N	f	2026-04-30 02:59:07.358821	\N	\N
9	Sparkle	Manufacturing	Tomas	01030845657ss@gmail.com	01234567890	BOTH	ACTIVE	\N	\N	f	2026-04-30 17:17:45.109036	\N	\N
10	Sparkle	Manufacturing	Tomas	01030845657ss@gmail.com	01234567890	BOTH	ACTIVE	\N	\N	f	2026-04-30 17:18:10.282285	\N	\N
11	Sparkle	Manufacturing	Tomas	01030845657ss@gmail.com	01234567890	BOTH	ACTIVE	\N	\N	f	2026-04-30 17:18:33.95427	\N	\N
12	Sparkle	Manufacturing	Tomas	01030845657ss@gmail.com	01234567890	BOTH	ACTIVE	\N	\N	f	2026-04-30 17:18:47.632532	\N	\N
13	Sparkle	Manufacturing	Tomas	01030845657ss@gmail.com	01234567890	BOTH	ACTIVE	\N	\N	f	2026-04-30 17:18:48.886535	\N	\N
14	salmaTesting	Manufacturing	Salma Yasser	01030845657ss@gmail.com	01234567890	BOTH	ACTIVE	\N	\N	f	2026-04-30 17:19:24.097796	\N	\N
15	salmaTesting	Manufacturing	Salma Yasser	01030845657ss@gmail.com	01234567890	BOTH	ACTIVE	\N	\N	f	2026-04-30 17:19:25.450872	\N	\N
16	Sparkle	Manufacturing	Mo Yasser	moyassermostafa2007@gmail.com	01234567890	BOTH	ACTIVE	\N	\N	f	2026-04-30 17:29:25.826138	\N	\N
\.


--
-- Data for Name: organization_request; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.organization_request (id, company_name, industry, contact_person_name, email, phone, requested_service, status, created_at, reviewed_at) FROM stdin;
1	Future Factory	Manufacturing	Sara Ahmed	sara@futurefactory.com	01012345678	BOTH	APPROVED	2026-04-21 01:33:41.545814	2026-04-21 01:45:07.954397
2	Test Company	Manufacturing	Mohamed Ali	mohamed@test.com	+20 123 456 7890	BOTH	PENDING	2026-04-22 19:28:15.165504	\N
3	Test Company	\N	Test Admin	newadmin@test.com	01234567890	BOTH	APPROVED	2026-04-25 20:00:15.245956	2026-04-25 20:01:17.791687
4	Test Company 2	\N	Test Person	01030845657ss@gmail.com	01234567890	BOTH	APPROVED	2026-04-25 20:40:44.14107	2026-04-25 20:42:10.841066
5	Test Company 2	\N	Test Person	01030845657ss@gmail.com	01234567890	BOTH	APPROVED	2026-04-25 20:45:19.300757	2026-04-25 20:45:47.623915
6	Ola Farhat	\N	Admin	olafarhat579@gmail.com	01234567890	BOTH	APPROVED	2026-04-28 08:19:29.393008	2026-04-28 08:20:13.038471
7	Ola	Manufacturing	Ola Farhat	olafarhat579@gmail.com	01234567890	BOTH	APPROVED	2026-04-28 08:25:57.791331	2026-04-28 08:26:42.448685
8	salmaTesting	Manufacturing	Salma Yasser	01030845657ss@gmail.com	01234567890	BOTH	APPROVED	2026-04-30 02:54:50.087583	2026-04-30 02:59:07.449173
9	Sparkle	Manufacturing	Tomas	01030845657ss@gmail.com	01234567890	BOTH	APPROVED	2026-04-30 17:16:21.728943	2026-04-30 17:16:45.510045
10	Sparkle	Manufacturing	Mo Yasser	moyasser2007@gmail.com	01234567890	BOTH	APPROVED	2026-04-30 17:21:41.217687	2026-04-30 17:22:51.250825
11	Sparkle	Manufacturing	Mo Yasser	moyassermostafa2007@gmail.co	01234567890	BOTH	APPROVED	2026-04-30 17:23:58.761163	2026-04-30 17:24:33.450279
12	Sparkle	Manufacturing	Mo Yasser	moyassermostafa2007@gmail.co	01234567890	BOTH	PENDING	2026-04-30 17:27:19.083888	\N
13	Sparkle	Manufacturing	Mo Yasser	moyassermostafa2007@gmail.com	01234567890	BOTH	APPROVED	2026-04-30 17:27:46.172531	2026-04-30 17:29:25.959021
\.


--
-- Data for Name: prediction; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.prediction (id, organization_id, machine_id, failure_probability, suggested_issue_type, severity, model_version, predicted_at, explanation, rul_cycles, ttf_hours) FROM stdin;
1	1	1	35.50	MECHANICAL	MEDIUM	\N	2026-04-21 02:34:22.417353	Possible bearing wear detected	150.00	48.00
2	4	7	72.50	MECHANICAL	HIGH	\N	2026-04-23 04:41:43.248668	Abnormal vibration detected in spindle. Bearing wear likely.	45.00	36.00
3	4	8	15.00	THERMAL	LOW	\N	2026-04-23 04:41:43.248668	Temperature within normal range. Minor monitoring advised.	200.00	180.00
4	4	9	91.00	MECHANICAL	CRITICAL	\N	2026-04-23 04:41:43.248668	Critical pressure and temperature spike. Immediate maintenance required.	10.00	8.00
5	4	10	8.00	ELECTRICAL	LOW	\N	2026-04-23 04:41:43.248668	Motor performing normally. No action needed.	300.00	250.00
6	4	11	58.00	THERMAL	MEDIUM	\N	2026-04-23 04:41:43.248668	Temperature trending upward. Preventive maintenance suggested within 3 days.	80.00	60.00
7	1	1	89.50	MECHANICAL	LOW	\N	2026-04-29 21:12:12.007015	Machine operating normally (Problem: sensor_3)	203.73	4889.52
8	4	7	89.50	MECHANICAL	LOW	\N	2026-04-29 21:12:13.086804	Machine operating normally (Problem: sensor_3)	203.73	4889.52
9	4	8	89.50	MECHANICAL	LOW	\N	2026-04-29 21:12:13.79378	Machine operating normally (Problem: sensor_1)	203.73	4889.52
10	4	9	89.50	MECHANICAL	LOW	\N	2026-04-29 21:12:14.390253	Machine operating normally (Problem: sensor_2)	203.73	4889.52
11	4	10	89.50	MECHANICAL	LOW	\N	2026-04-29 21:12:14.997011	Machine operating normally (Problem: sensor_1)	203.73	4889.52
12	4	11	89.50	MECHANICAL	LOW	\N	2026-04-29 21:12:15.59016	Machine operating normally (Problem: sensor_1)	203.73	4889.52
13	1	1	89.50	MECHANICAL	LOW	\N	2026-04-29 21:22:11.584869	Machine operating normally (Problem: sensor_3)	203.73	4889.52
14	4	8	89.50	MECHANICAL	LOW	\N	2026-04-29 21:22:12.174321	Machine operating normally (Problem: sensor_1)	203.73	4889.52
15	4	10	89.50	MECHANICAL	LOW	\N	2026-04-29 21:22:12.768084	Machine operating normally (Problem: sensor_1)	203.73	4889.52
16	4	7	89.50	MECHANICAL	LOW	\N	2026-04-29 21:22:13.275717	Machine operating normally (Problem: sensor_3)	203.73	4889.52
17	4	9	89.50	MECHANICAL	LOW	\N	2026-04-29 21:22:13.893944	Machine operating normally (Problem: sensor_2)	203.73	4889.52
18	4	11	89.50	MECHANICAL	LOW	\N	2026-04-29 21:22:14.471813	Machine operating normally (Problem: sensor_1)	203.73	4889.52
19	1	1	89.50	MECHANICAL	LOW	\N	2026-04-29 21:24:44.641782	Machine operating normally (Problem: sensor_3)	203.73	4889.52
20	4	8	89.50	MECHANICAL	LOW	\N	2026-04-29 21:24:45.362181	Machine operating normally (Problem: sensor_1)	203.73	4889.52
21	4	10	89.50	MECHANICAL	LOW	\N	2026-04-29 21:24:46.079629	Machine operating normally (Problem: sensor_1)	203.73	4889.52
22	4	7	89.50	MECHANICAL	LOW	\N	2026-04-29 21:24:46.691143	Machine operating normally (Problem: sensor_3)	203.73	4889.52
23	4	9	89.50	MECHANICAL	LOW	\N	2026-04-29 21:24:47.285741	Machine operating normally (Problem: sensor_2)	203.73	4889.52
24	4	11	89.50	MECHANICAL	LOW	\N	2026-04-29 21:24:47.883067	Machine operating normally (Problem: sensor_1)	203.73	4889.52
25	1	1	89.50	MECHANICAL	LOW	\N	2026-04-29 21:34:42.864033	Machine operating normally (Problem: sensor_3)	203.73	4889.52
26	4	8	89.50	MECHANICAL	LOW	\N	2026-04-29 21:34:43.452353	Machine operating normally (Problem: sensor_1)	203.73	4889.52
27	4	10	89.50	MECHANICAL	LOW	\N	2026-04-29 21:34:43.982774	Machine operating normally (Problem: sensor_1)	203.73	4889.52
28	4	7	89.50	MECHANICAL	LOW	\N	2026-04-29 21:34:44.571941	Machine operating normally (Problem: sensor_3)	203.73	4889.52
29	4	9	89.50	MECHANICAL	LOW	\N	2026-04-29 21:34:45.110974	Machine operating normally (Problem: sensor_2)	203.73	4889.52
30	4	11	89.50	MECHANICAL	LOW	\N	2026-04-29 21:34:45.674553	Machine operating normally (Problem: sensor_1)	203.73	4889.52
31	1	1	89.50	MECHANICAL	LOW	\N	2026-04-29 21:44:42.284392	Machine operating normally (Problem: sensor_3)	203.73	4889.52
32	4	8	92.70	MECHANICAL	LOW	\N	2026-04-29 21:44:42.903394	Machine operating normally (Problem: sensor_14)	203.51	4884.24
33	4	10	89.60	MECHANICAL	LOW	\N	2026-04-29 21:44:43.896408	Machine operating normally (Problem: sensor_6)	191.15	4587.60
34	4	7	86.50	MECHANICAL	LOW	\N	2026-04-29 21:44:44.517009	Machine operating normally (Problem: sensor_3)	201.33	4831.92
35	4	9	92.70	MECHANICAL	LOW	\N	2026-04-29 21:44:45.126345	Machine operating normally (Problem: sensor_2)	203.51	4884.24
36	4	11	86.50	MECHANICAL	LOW	\N	2026-04-29 21:44:45.879263	Machine operating normally (Problem: sensor_3)	201.33	4831.92
37	4	8	71.10	MECHANICAL	LOW	\N	2026-04-29 21:45:51.215634	Machine operating normally (Problem: sensor_12)	88.08	2113.92
38	4	10	86.50	MECHANICAL	LOW	\N	2026-04-29 21:45:51.974541	Machine operating normally (Problem: sensor_4)	201.33	4831.92
39	4	7	71.40	MECHANICAL	LOW	\N	2026-04-29 21:45:52.577173	Machine operating normally (Problem: sensor_21)	88.47	2123.28
40	4	9	79.80	MECHANICAL	LOW	\N	2026-04-29 21:45:53.298682	Machine operating normally (Problem: sensor_5)	86.36	2072.64
41	4	11	70.40	MECHANICAL	LOW	\N	2026-04-29 21:45:53.970967	Machine operating normally (Problem: sensor_1)	79.52	1908.48
42	4	8	71.10	MECHANICAL	LOW	\N	2026-04-29 21:47:24.921065	Machine operating normally (Problem: sensor_12)	88.08	2113.92
43	4	10	86.50	MECHANICAL	LOW	\N	2026-04-29 21:47:25.606423	Machine operating normally (Problem: sensor_4)	201.33	4831.92
44	4	7	71.40	MECHANICAL	LOW	\N	2026-04-29 21:47:26.190072	Machine operating normally (Problem: sensor_21)	88.47	2123.28
45	4	9	79.80	MECHANICAL	LOW	\N	2026-04-29 21:47:26.898687	Machine operating normally (Problem: sensor_5)	86.36	2072.64
46	4	11	70.40	MECHANICAL	LOW	\N	2026-04-29 21:47:27.498788	Machine operating normally (Problem: sensor_1)	79.52	1908.48
47	4	8	71.10	MECHANICAL	LOW	\N	2026-04-29 21:57:24.730075	Machine operating normally (Problem: sensor_12)	88.08	2113.92
48	4	10	86.50	MECHANICAL	LOW	\N	2026-04-29 21:57:25.579897	Machine operating normally (Problem: sensor_4)	201.33	4831.92
49	4	7	71.40	MECHANICAL	LOW	\N	2026-04-29 21:57:26.297676	Machine operating normally (Problem: sensor_21)	88.47	2123.28
50	4	9	100.00	MECHANICAL	LOW	\N	2026-04-29 21:57:27.027957	Machine operating normally (Problem: sensor_9)	159.10	3818.40
51	4	11	70.40	MECHANICAL	LOW	\N	2026-04-29 21:57:27.794462	Machine operating normally (Problem: sensor_1)	79.52	1908.48
52	4	8	100.00	MECHANICAL	LOW	\N	2026-04-29 22:07:24.10064	Machine operating normally (Problem: sensor_9)	159.10	3818.40
53	4	10	56.40	MECHANICAL	LOW	\N	2026-04-29 22:07:24.695067	Machine operating normally (Problem: sensor_9)	192.20	4612.80
54	4	7	48.80	MECHANICAL	MEDIUM	\N	2026-04-29 22:07:25.331607	Warning: abnormal behavior detected near sensor_9. Schedule inspection. (Problem: sensor_9)	24.95	598.80
55	4	9	50.00	MECHANICAL	HIGH	\N	2026-04-29 22:07:26.418481	CRITICAL: inspect sensor_9 immediately. (Problem: sensor_9)	7.63	183.12
56	4	11	51.20	MECHANICAL	LOW	\N	2026-04-29 22:07:27.118852	Machine operating normally (Problem: sensor_9)	46.53	1116.72
57	4	8	100.00	MECHANICAL	LOW	\N	2026-04-29 22:17:24.170103	Machine operating normally (Problem: sensor_9)	159.10	3818.40
58	4	10	56.40	MECHANICAL	LOW	\N	2026-04-29 22:17:24.702487	Machine operating normally (Problem: sensor_9)	192.20	4612.80
59	4	11	51.20	MECHANICAL	LOW	\N	2026-04-29 22:17:25.835669	Machine operating normally (Problem: sensor_9)	46.53	1116.72
60	4	7	48.80	MECHANICAL	MEDIUM	\N	2026-04-29 22:17:26.639105	Warning: abnormal behavior detected near sensor_9. Schedule inspection. (Problem: sensor_9)	24.95	598.80
61	4	9	50.00	MECHANICAL	HIGH	\N	2026-04-29 22:17:27.335146	CRITICAL: inspect sensor_9 immediately. (Problem: sensor_9)	7.63	183.12
62	4	8	100.00	MECHANICAL	LOW	\N	2026-04-29 22:27:24.627876	Machine operating normally (Problem: sensor_9)	159.10	3818.40
63	4	10	56.40	MECHANICAL	LOW	\N	2026-04-29 22:27:25.353652	Machine operating normally (Problem: sensor_9)	192.20	4612.80
64	4	11	51.20	MECHANICAL	LOW	\N	2026-04-29 22:27:26.094281	Machine operating normally (Problem: sensor_9)	46.53	1116.72
65	4	7	48.80	MECHANICAL	MEDIUM	\N	2026-04-29 22:27:26.631521	Warning: abnormal behavior detected near sensor_9. Schedule inspection. (Problem: sensor_9)	24.95	598.80
66	4	9	50.00	MECHANICAL	HIGH	\N	2026-04-29 22:27:27.288522	CRITICAL: inspect sensor_9 immediately. (Problem: sensor_9)	7.63	183.12
67	4	8	100.00	MECHANICAL	LOW	\N	2026-04-29 22:37:24.265652	Machine operating normally (Problem: sensor_9)	159.10	3818.40
68	4	10	56.40	MECHANICAL	LOW	\N	2026-04-29 22:37:24.930044	Machine operating normally (Problem: sensor_9)	192.20	4612.80
69	4	11	51.20	MECHANICAL	LOW	\N	2026-04-29 22:37:26.012238	Machine operating normally (Problem: sensor_9)	46.53	1116.72
70	4	7	48.80	MECHANICAL	MEDIUM	\N	2026-04-29 22:37:26.608076	Warning: abnormal behavior detected near sensor_9. Schedule inspection. (Problem: sensor_9)	24.95	598.80
71	4	9	50.00	MECHANICAL	HIGH	\N	2026-04-29 22:37:27.108769	CRITICAL: inspect sensor_9 immediately. (Problem: sensor_9)	7.63	183.12
72	4	8	100.00	MECHANICAL	LOW	\N	2026-04-29 23:06:53.2835	Machine operating normally (Problem: sensor_9)	159.10	3818.40
73	4	10	56.40	MECHANICAL	LOW	\N	2026-04-29 23:06:53.969621	Machine operating normally (Problem: sensor_9)	192.20	4612.80
74	4	11	51.20	MECHANICAL	LOW	\N	2026-04-29 23:06:54.573615	Machine operating normally (Problem: sensor_9)	46.53	1116.72
75	4	7	48.80	MECHANICAL	MEDIUM	\N	2026-04-29 23:06:55.171666	Warning: abnormal behavior detected near sensor_9. Schedule inspection. (Problem: sensor_9)	24.95	598.80
76	4	9	50.00	MECHANICAL	HIGH	\N	2026-04-29 23:06:56.186049	CRITICAL: inspect sensor_9 immediately. (Problem: sensor_9)	7.63	183.12
77	4	8	100.00	MECHANICAL	LOW	\N	2026-04-29 23:06:56.765812	Machine operating normally (Problem: sensor_9)	159.10	3818.40
78	4	10	56.40	MECHANICAL	LOW	\N	2026-04-29 23:06:57.266882	Machine operating normally (Problem: sensor_9)	192.20	4612.80
79	4	11	51.20	MECHANICAL	LOW	\N	2026-04-29 23:06:57.789957	Machine operating normally (Problem: sensor_9)	46.53	1116.72
80	4	7	48.80	MECHANICAL	MEDIUM	\N	2026-04-29 23:06:58.380026	Warning: abnormal behavior detected near sensor_9. Schedule inspection. (Problem: sensor_9)	24.95	598.80
81	4	9	50.00	MECHANICAL	HIGH	\N	2026-04-29 23:06:58.994228	CRITICAL: inspect sensor_9 immediately. (Problem: sensor_9)	7.63	183.12
82	4	8	100.00	MECHANICAL	LOW	\N	2026-04-29 23:07:24.288457	Machine operating normally (Problem: sensor_9)	159.10	3818.40
83	4	10	56.40	MECHANICAL	LOW	\N	2026-04-29 23:07:24.771488	Machine operating normally (Problem: sensor_9)	192.20	4612.80
84	4	11	51.20	MECHANICAL	LOW	\N	2026-04-29 23:07:25.384446	Machine operating normally (Problem: sensor_9)	46.53	1116.72
85	4	7	48.80	MECHANICAL	MEDIUM	\N	2026-04-29 23:07:25.993204	Warning: abnormal behavior detected near sensor_9. Schedule inspection. (Problem: sensor_9)	24.95	598.80
86	4	9	50.00	MECHANICAL	HIGH	\N	2026-04-29 23:07:26.591311	CRITICAL: inspect sensor_9 immediately. (Problem: sensor_9)	7.63	183.12
87	4	8	100.00	MECHANICAL	LOW	\N	2026-04-29 23:17:24.497399	Machine operating normally (Problem: sensor_9)	159.10	3818.40
88	4	10	56.40	MECHANICAL	LOW	\N	2026-04-29 23:17:25.006883	Machine operating normally (Problem: sensor_9)	192.20	4612.80
89	4	11	51.20	MECHANICAL	LOW	\N	2026-04-29 23:17:25.570225	Machine operating normally (Problem: sensor_9)	46.53	1116.72
90	4	7	48.80	MECHANICAL	MEDIUM	\N	2026-04-29 23:17:26.174415	Warning: abnormal behavior detected near sensor_9. Schedule inspection. (Problem: sensor_9)	24.95	598.80
91	4	9	50.00	MECHANICAL	HIGH	\N	2026-04-29 23:17:26.799776	CRITICAL: inspect sensor_9 immediately. (Problem: sensor_9)	7.63	183.12
92	4	8	100.00	MECHANICAL	LOW	\N	2026-04-29 23:27:24.472005	Machine operating normally (Problem: sensor_9)	159.10	3818.40
93	4	10	56.40	MECHANICAL	LOW	\N	2026-04-29 23:27:25.073739	Machine operating normally (Problem: sensor_9)	192.20	4612.80
94	4	11	51.20	MECHANICAL	LOW	\N	2026-04-29 23:27:25.716786	Machine operating normally (Problem: sensor_9)	46.53	1116.72
95	4	7	48.80	MECHANICAL	MEDIUM	\N	2026-04-29 23:27:26.324296	Warning: abnormal behavior detected near sensor_9. Schedule inspection. (Problem: sensor_9)	24.95	598.80
96	4	9	50.00	MECHANICAL	HIGH	\N	2026-04-29 23:27:26.9303	CRITICAL: inspect sensor_9 immediately. (Problem: sensor_9)	7.63	183.12
97	4	8	100.00	MECHANICAL	LOW	\N	2026-04-29 23:37:23.987795	Machine operating normally (Problem: sensor_9)	159.10	3818.40
98	4	10	56.40	MECHANICAL	LOW	\N	2026-04-29 23:37:24.994619	Machine operating normally (Problem: sensor_9)	192.20	4612.80
99	4	11	51.20	MECHANICAL	LOW	\N	2026-04-29 23:37:25.732952	Machine operating normally (Problem: sensor_9)	46.53	1116.72
100	4	7	48.80	MECHANICAL	MEDIUM	\N	2026-04-29 23:37:26.398303	Warning: abnormal behavior detected near sensor_9. Schedule inspection. (Problem: sensor_9)	24.95	598.80
101	4	9	50.00	MECHANICAL	HIGH	\N	2026-04-29 23:37:26.918792	CRITICAL: inspect sensor_9 immediately. (Problem: sensor_9)	7.63	183.12
102	4	8	100.00	MECHANICAL	LOW	\N	2026-04-29 23:47:24.425058	Machine operating normally (Problem: sensor_9)	159.10	3818.40
103	4	10	56.40	MECHANICAL	LOW	\N	2026-04-29 23:47:25.031108	Machine operating normally (Problem: sensor_9)	192.20	4612.80
104	4	11	51.20	MECHANICAL	LOW	\N	2026-04-29 23:47:25.624934	Machine operating normally (Problem: sensor_9)	46.53	1116.72
105	4	7	48.80	MECHANICAL	MEDIUM	\N	2026-04-29 23:47:26.237378	Warning: abnormal behavior detected near sensor_9. Schedule inspection. (Problem: sensor_9)	24.95	598.80
106	4	9	50.00	MECHANICAL	HIGH	\N	2026-04-29 23:47:26.70631	CRITICAL: inspect sensor_9 immediately. (Problem: sensor_9)	7.63	183.12
107	4	8	100.00	MECHANICAL	LOW	\N	2026-04-29 23:57:24.417539	Machine operating normally (Problem: sensor_9)	159.10	3818.40
108	4	10	56.40	MECHANICAL	LOW	\N	2026-04-29 23:57:24.903007	Machine operating normally (Problem: sensor_9)	192.20	4612.80
109	4	11	51.20	MECHANICAL	LOW	\N	2026-04-29 23:57:25.588651	Machine operating normally (Problem: sensor_9)	46.53	1116.72
110	4	7	48.80	MECHANICAL	MEDIUM	\N	2026-04-29 23:57:26.118312	Warning: abnormal behavior detected near sensor_9. Schedule inspection. (Problem: sensor_9)	24.95	598.80
111	4	9	50.00	MECHANICAL	HIGH	\N	2026-04-29 23:57:26.69469	CRITICAL: inspect sensor_9 immediately. (Problem: sensor_9)	7.63	183.12
112	4	8	100.00	MECHANICAL	LOW	\N	2026-04-30 00:07:24.058521	Machine operating normally (Problem: sensor_9)	159.10	3818.40
113	4	10	56.40	MECHANICAL	LOW	\N	2026-04-30 00:07:24.694073	Machine operating normally (Problem: sensor_9)	192.20	4612.80
114	4	11	51.20	MECHANICAL	LOW	\N	2026-04-30 00:07:25.301072	Machine operating normally (Problem: sensor_9)	46.53	1116.72
115	4	7	48.80	MECHANICAL	MEDIUM	\N	2026-04-30 00:07:25.901136	Warning: abnormal behavior detected near sensor_9. Schedule inspection. (Problem: sensor_9)	24.95	598.80
116	4	9	50.00	MECHANICAL	HIGH	\N	2026-04-30 00:07:26.515347	CRITICAL: inspect sensor_9 immediately. (Problem: sensor_9)	7.63	183.12
117	4	8	100.00	MECHANICAL	LOW	\N	2026-04-30 00:17:47.320446	Machine operating normally (Problem: sensor_9)	159.10	3818.40
118	4	10	56.40	MECHANICAL	LOW	\N	2026-04-30 00:17:47.824082	Machine operating normally (Problem: sensor_9)	192.20	4612.80
119	4	11	51.20	MECHANICAL	LOW	\N	2026-04-30 00:17:48.332075	Machine operating normally (Problem: sensor_9)	46.53	1116.72
120	4	7	48.80	MECHANICAL	MEDIUM	\N	2026-04-30 00:17:49.353123	Warning: abnormal behavior detected near sensor_9. Schedule inspection. (Problem: sensor_9)	24.95	598.80
121	4	9	50.00	MECHANICAL	HIGH	\N	2026-04-30 00:17:49.847527	CRITICAL: inspect sensor_9 immediately. (Problem: sensor_9)	7.63	183.12
122	4	8	100.00	MECHANICAL	LOW	\N	2026-04-30 00:27:21.331434	Machine operating normally (Problem: sensor_9)	159.10	3818.40
123	4	10	56.40	MECHANICAL	LOW	\N	2026-04-30 00:27:21.835472	Machine operating normally (Problem: sensor_9)	192.20	4612.80
124	4	11	51.20	MECHANICAL	LOW	\N	2026-04-30 00:27:22.869818	Machine operating normally (Problem: sensor_9)	46.53	1116.72
125	4	7	48.80	MECHANICAL	MEDIUM	\N	2026-04-30 00:27:23.328043	Warning: abnormal behavior detected near sensor_9. Schedule inspection. (Problem: sensor_9)	24.95	598.80
126	4	9	50.00	MECHANICAL	HIGH	\N	2026-04-30 00:27:23.828256	CRITICAL: inspect sensor_9 immediately. (Problem: sensor_9)	7.63	183.12
127	4	8	100.00	MECHANICAL	LOW	\N	2026-04-30 00:37:21.536558	Machine operating normally (Problem: sensor_9)	159.10	3818.40
128	4	10	56.40	MECHANICAL	LOW	\N	2026-04-30 00:37:22.235046	Machine operating normally (Problem: sensor_9)	192.20	4612.80
129	4	11	51.20	MECHANICAL	LOW	\N	2026-04-30 00:37:22.739316	Machine operating normally (Problem: sensor_9)	46.53	1116.72
130	4	7	48.80	MECHANICAL	MEDIUM	\N	2026-04-30 00:37:23.235816	Warning: abnormal behavior detected near sensor_9. Schedule inspection. (Problem: sensor_9)	24.95	598.80
131	4	9	50.00	MECHANICAL	HIGH	\N	2026-04-30 00:37:23.739007	CRITICAL: inspect sensor_9 immediately. (Problem: sensor_9)	7.63	183.12
132	4	8	100.00	MECHANICAL	LOW	\N	2026-04-30 00:47:21.767891	Machine operating normally (Problem: sensor_9)	159.10	3818.40
133	4	10	56.40	MECHANICAL	LOW	\N	2026-04-30 00:47:22.341739	Machine operating normally (Problem: sensor_9)	192.20	4612.80
134	4	11	51.20	MECHANICAL	LOW	\N	2026-04-30 00:47:22.959311	Machine operating normally (Problem: sensor_9)	46.53	1116.72
135	4	7	48.80	MECHANICAL	MEDIUM	\N	2026-04-30 00:47:23.462586	Warning: abnormal behavior detected near sensor_9. Schedule inspection. (Problem: sensor_9)	24.95	598.80
136	4	9	50.00	MECHANICAL	HIGH	\N	2026-04-30 00:47:23.969304	CRITICAL: inspect sensor_9 immediately. (Problem: sensor_9)	7.63	183.12
137	4	8	100.00	MECHANICAL	LOW	\N	2026-04-30 00:57:21.974774	Machine operating normally (Problem: sensor_9)	159.10	3818.40
138	4	10	56.40	MECHANICAL	LOW	\N	2026-04-30 00:57:22.474371	Machine operating normally (Problem: sensor_9)	192.20	4612.80
139	4	11	51.20	MECHANICAL	LOW	\N	2026-04-30 00:57:23.179291	Machine operating normally (Problem: sensor_9)	46.53	1116.72
140	4	7	48.80	MECHANICAL	MEDIUM	\N	2026-04-30 00:57:23.770626	Warning: abnormal behavior detected near sensor_9. Schedule inspection. (Problem: sensor_9)	24.95	598.80
141	4	9	50.00	MECHANICAL	HIGH	\N	2026-04-30 00:57:24.274839	CRITICAL: inspect sensor_9 immediately. (Problem: sensor_9)	7.63	183.12
142	4	8	100.00	MECHANICAL	LOW	\N	2026-04-30 01:07:21.897247	Machine operating normally (Problem: sensor_9)	159.10	3818.40
143	4	10	56.40	MECHANICAL	LOW	\N	2026-04-30 01:07:22.353486	Machine operating normally (Problem: sensor_9)	192.20	4612.80
144	4	11	51.20	MECHANICAL	LOW	\N	2026-04-30 01:07:22.884354	Machine operating normally (Problem: sensor_9)	46.53	1116.72
145	4	7	48.80	MECHANICAL	MEDIUM	\N	2026-04-30 01:07:23.448443	Warning: abnormal behavior detected near sensor_9. Schedule inspection. (Problem: sensor_9)	24.95	598.80
146	4	9	50.00	MECHANICAL	HIGH	\N	2026-04-30 01:07:24.088269	CRITICAL: inspect sensor_9 immediately. (Problem: sensor_9)	7.63	183.12
147	4	8	100.00	MECHANICAL	LOW	\N	2026-04-30 01:17:21.368872	Machine operating normally (Problem: sensor_9)	159.10	3818.40
148	4	10	56.40	MECHANICAL	LOW	\N	2026-04-30 01:17:22.399799	Machine operating normally (Problem: sensor_9)	192.20	4612.80
149	4	11	51.20	MECHANICAL	LOW	\N	2026-04-30 01:17:22.897324	Machine operating normally (Problem: sensor_9)	46.53	1116.72
150	4	7	48.80	MECHANICAL	MEDIUM	\N	2026-04-30 01:17:23.469064	Warning: abnormal behavior detected near sensor_9. Schedule inspection. (Problem: sensor_9)	24.95	598.80
151	4	9	50.00	MECHANICAL	HIGH	\N	2026-04-30 01:17:23.966201	CRITICAL: inspect sensor_9 immediately. (Problem: sensor_9)	7.63	183.12
152	4	8	100.00	MECHANICAL	LOW	\N	2026-04-30 01:27:21.938025	Machine operating normally (Problem: sensor_9)	159.10	3818.40
153	4	10	56.40	MECHANICAL	LOW	\N	2026-04-30 01:27:22.569663	Machine operating normally (Problem: sensor_9)	192.20	4612.80
154	4	11	51.20	MECHANICAL	LOW	\N	2026-04-30 01:27:23.33254	Machine operating normally (Problem: sensor_9)	46.53	1116.72
155	4	7	48.80	MECHANICAL	MEDIUM	\N	2026-04-30 01:27:23.995154	Warning: abnormal behavior detected near sensor_9. Schedule inspection. (Problem: sensor_9)	24.95	598.80
156	4	9	50.00	MECHANICAL	HIGH	\N	2026-04-30 01:27:24.591658	CRITICAL: inspect sensor_9 immediately. (Problem: sensor_9)	7.63	183.12
157	4	8	100.00	MECHANICAL	LOW	\N	2026-04-30 01:37:22.186142	Machine operating normally (Problem: sensor_9)	159.10	3818.40
158	4	10	56.40	MECHANICAL	LOW	\N	2026-04-30 01:37:22.681734	Machine operating normally (Problem: sensor_9)	192.20	4612.80
159	4	11	51.20	MECHANICAL	LOW	\N	2026-04-30 01:37:23.350021	Machine operating normally (Problem: sensor_9)	46.53	1116.72
160	4	7	48.80	MECHANICAL	MEDIUM	\N	2026-04-30 01:37:23.996531	Warning: abnormal behavior detected near sensor_9. Schedule inspection. (Problem: sensor_9)	24.95	598.80
161	4	9	50.00	MECHANICAL	HIGH	\N	2026-04-30 01:37:24.665034	CRITICAL: inspect sensor_9 immediately. (Problem: sensor_9)	7.63	183.12
162	4	8	100.00	MECHANICAL	LOW	\N	2026-04-30 01:47:21.899756	Machine operating normally (Problem: sensor_9)	159.10	3818.40
163	4	10	56.40	MECHANICAL	LOW	\N	2026-04-30 01:47:22.59579	Machine operating normally (Problem: sensor_9)	192.20	4612.80
164	4	11	51.20	MECHANICAL	LOW	\N	2026-04-30 01:47:23.295451	Machine operating normally (Problem: sensor_9)	46.53	1116.72
165	4	7	48.80	MECHANICAL	MEDIUM	\N	2026-04-30 01:47:23.77355	Warning: abnormal behavior detected near sensor_9. Schedule inspection. (Problem: sensor_9)	24.95	598.80
166	4	9	50.00	MECHANICAL	HIGH	\N	2026-04-30 01:47:24.435885	CRITICAL: inspect sensor_9 immediately. (Problem: sensor_9)	7.63	183.12
167	4	8	100.00	MECHANICAL	LOW	\N	2026-04-30 01:57:42.906432	Machine operating normally (Problem: sensor_9)	159.10	3818.40
168	4	10	56.40	MECHANICAL	LOW	\N	2026-04-30 01:57:43.5398	Machine operating normally (Problem: sensor_9)	192.20	4612.80
169	4	11	51.20	MECHANICAL	LOW	\N	2026-04-30 01:57:44.542587	Machine operating normally (Problem: sensor_9)	46.53	1116.72
170	4	7	48.80	MECHANICAL	MEDIUM	\N	2026-04-30 01:57:45.143196	Warning: abnormal behavior detected near sensor_9. Schedule inspection. (Problem: sensor_9)	24.95	598.80
171	4	9	50.00	MECHANICAL	HIGH	\N	2026-04-30 01:57:45.614722	CRITICAL: inspect sensor_9 immediately. (Problem: sensor_9)	7.63	183.12
172	4	8	100.00	MECHANICAL	LOW	\N	2026-04-30 02:07:19.121718	Machine operating normally (Problem: sensor_9)	159.10	3818.40
173	4	10	56.40	MECHANICAL	LOW	\N	2026-04-30 02:07:19.637223	Machine operating normally (Problem: sensor_9)	192.20	4612.80
174	4	11	51.20	MECHANICAL	LOW	\N	2026-04-30 02:07:20.132208	Machine operating normally (Problem: sensor_9)	46.53	1116.72
175	4	7	48.80	MECHANICAL	MEDIUM	\N	2026-04-30 02:07:20.759899	Warning: abnormal behavior detected near sensor_9. Schedule inspection. (Problem: sensor_9)	24.95	598.80
176	4	9	50.00	MECHANICAL	HIGH	\N	2026-04-30 02:07:21.257586	CRITICAL: inspect sensor_9 immediately. (Problem: sensor_9)	7.63	183.12
177	4	8	100.00	MECHANICAL	LOW	\N	2026-04-30 02:17:18.687109	Machine operating normally (Problem: sensor_9)	159.10	3818.40
178	4	10	56.40	MECHANICAL	LOW	\N	2026-04-30 02:17:19.661342	Machine operating normally (Problem: sensor_9)	192.20	4612.80
179	4	11	51.20	MECHANICAL	LOW	\N	2026-04-30 02:17:20.324305	Machine operating normally (Problem: sensor_9)	46.53	1116.72
180	4	7	48.80	MECHANICAL	MEDIUM	\N	2026-04-30 02:17:20.83214	Warning: abnormal behavior detected near sensor_9. Schedule inspection. (Problem: sensor_9)	24.95	598.80
181	4	9	50.00	MECHANICAL	HIGH	\N	2026-04-30 02:17:21.435939	CRITICAL: inspect sensor_9 immediately. (Problem: sensor_9)	7.63	183.12
182	4	8	100.00	MECHANICAL	LOW	\N	2026-04-30 02:27:18.535933	Machine operating normally (Problem: sensor_9)	159.10	3818.40
183	4	10	56.40	MECHANICAL	LOW	\N	2026-04-30 02:27:19.07002	Machine operating normally (Problem: sensor_9)	192.20	4612.80
184	4	11	51.20	MECHANICAL	LOW	\N	2026-04-30 02:27:19.949803	Machine operating normally (Problem: sensor_9)	46.53	1116.72
185	4	7	48.80	MECHANICAL	MEDIUM	\N	2026-04-30 02:27:20.501039	Warning: abnormal behavior detected near sensor_9. Schedule inspection. (Problem: sensor_9)	24.95	598.80
186	4	9	50.00	MECHANICAL	HIGH	\N	2026-04-30 02:27:21.118398	CRITICAL: inspect sensor_9 immediately. (Problem: sensor_9)	7.63	183.12
187	4	8	100.00	MECHANICAL	LOW	\N	2026-04-30 02:34:17.770159	Machine operating normally (Problem: sensor_9)	159.10	3818.40
188	4	10	56.40	MECHANICAL	LOW	\N	2026-04-30 02:34:18.440713	Machine operating normally (Problem: sensor_9)	192.20	4612.80
189	4	11	51.20	MECHANICAL	LOW	\N	2026-04-30 02:34:19.340559	Machine operating normally (Problem: sensor_9)	46.53	1116.72
190	4	7	48.80	MECHANICAL	MEDIUM	\N	2026-04-30 02:34:19.94061	Warning: abnormal behavior detected near sensor_9. Schedule inspection. (Problem: sensor_9)	24.95	598.80
191	4	9	50.00	MECHANICAL	HIGH	\N	2026-04-30 02:34:20.451639	CRITICAL: inspect sensor_9 immediately. (Problem: sensor_9)	7.63	183.12
192	4	8	100.00	MECHANICAL	LOW	\N	2026-04-30 02:44:08.459997	Machine operating normally (Problem: sensor_9)	159.10	3818.40
193	4	10	56.40	MECHANICAL	LOW	\N	2026-04-30 02:44:09.056362	Machine operating normally (Problem: sensor_9)	192.20	4612.80
194	4	11	51.20	MECHANICAL	LOW	\N	2026-04-30 02:44:09.657344	Machine operating normally (Problem: sensor_9)	46.53	1116.72
195	4	7	48.80	MECHANICAL	MEDIUM	\N	2026-04-30 02:44:10.159834	Warning: abnormal behavior detected near sensor_9. Schedule inspection. (Problem: sensor_9)	24.95	598.80
196	4	9	50.00	MECHANICAL	HIGH	\N	2026-04-30 02:44:10.66341	CRITICAL: inspect sensor_9 immediately. (Problem: sensor_9)	7.63	183.12
197	4	8	100.00	MECHANICAL	LOW	\N	2026-04-30 02:45:45.552566	Machine operating normally (Problem: sensor_9)	159.10	3818.40
198	4	10	56.40	MECHANICAL	LOW	\N	2026-04-30 02:45:46.043844	Machine operating normally (Problem: sensor_9)	192.20	4612.80
199	4	11	51.20	MECHANICAL	LOW	\N	2026-04-30 02:45:47.015973	Machine operating normally (Problem: sensor_9)	46.53	1116.72
200	4	7	48.80	MECHANICAL	MEDIUM	\N	2026-04-30 02:45:47.652649	Warning: abnormal behavior detected near sensor_9. Schedule inspection. (Problem: sensor_9)	24.95	598.80
201	4	9	50.00	MECHANICAL	HIGH	\N	2026-04-30 02:45:48.145647	CRITICAL: inspect sensor_9 immediately. (Problem: sensor_9)	7.63	183.12
202	4	8	100.00	MECHANICAL	LOW	\N	2026-04-30 02:45:49.546946	Machine operating normally (Problem: sensor_9)	159.10	3818.40
203	4	10	56.40	MECHANICAL	LOW	\N	2026-04-30 02:45:50.153175	Machine operating normally (Problem: sensor_9)	192.20	4612.80
204	4	11	51.20	MECHANICAL	LOW	\N	2026-04-30 02:45:50.757617	Machine operating normally (Problem: sensor_9)	46.53	1116.72
205	4	7	48.80	MECHANICAL	MEDIUM	\N	2026-04-30 02:45:51.451164	Warning: abnormal behavior detected near sensor_9. Schedule inspection. (Problem: sensor_9)	24.95	598.80
206	4	9	50.00	MECHANICAL	HIGH	\N	2026-04-30 02:45:52.138081	CRITICAL: inspect sensor_9 immediately. (Problem: sensor_9)	7.63	183.12
207	4	8	100.00	MECHANICAL	LOW	\N	2026-04-30 02:46:13.949947	Machine operating normally (Problem: sensor_9)	159.10	3818.40
208	4	10	56.40	MECHANICAL	LOW	\N	2026-04-30 02:46:14.96184	Machine operating normally (Problem: sensor_9)	192.20	4612.80
209	4	11	51.20	MECHANICAL	LOW	\N	2026-04-30 02:46:15.449777	Machine operating normally (Problem: sensor_9)	46.53	1116.72
210	4	7	48.80	MECHANICAL	MEDIUM	\N	2026-04-30 02:46:16.051664	Warning: abnormal behavior detected near sensor_9. Schedule inspection. (Problem: sensor_9)	24.95	598.80
211	4	9	50.00	MECHANICAL	HIGH	\N	2026-04-30 02:46:16.644009	CRITICAL: inspect sensor_9 immediately. (Problem: sensor_9)	7.63	183.12
212	4	8	100.00	MECHANICAL	LOW	\N	2026-04-30 02:46:35.475479	Machine operating normally (Problem: sensor_9)	159.10	3818.40
213	4	10	56.40	MECHANICAL	LOW	\N	2026-04-30 02:46:36.146399	Machine operating normally (Problem: sensor_9)	192.20	4612.80
214	4	11	51.20	MECHANICAL	LOW	\N	2026-04-30 02:46:36.648551	Machine operating normally (Problem: sensor_9)	46.53	1116.72
215	4	7	48.80	MECHANICAL	MEDIUM	\N	2026-04-30 02:46:37.145257	Warning: abnormal behavior detected near sensor_9. Schedule inspection. (Problem: sensor_9)	24.95	598.80
216	4	9	50.00	MECHANICAL	HIGH	\N	2026-04-30 02:46:37.743284	CRITICAL: inspect sensor_9 immediately. (Problem: sensor_9)	7.63	183.12
217	4	8	100.00	MECHANICAL	LOW	\N	2026-04-30 02:47:37.270252	Machine operating normally (Problem: sensor_9)	159.10	3818.40
218	4	10	56.40	MECHANICAL	LOW	\N	2026-04-30 02:47:37.845917	Machine operating normally (Problem: sensor_9)	192.20	4612.80
219	4	11	51.20	MECHANICAL	LOW	\N	2026-04-30 02:47:38.374559	Machine operating normally (Problem: sensor_9)	46.53	1116.72
220	4	7	48.80	MECHANICAL	MEDIUM	\N	2026-04-30 02:47:38.937765	Warning: abnormal behavior detected near sensor_9. Schedule inspection. (Problem: sensor_9)	24.95	598.80
221	4	9	50.00	MECHANICAL	HIGH	\N	2026-04-30 02:47:39.551335	CRITICAL: inspect sensor_9 immediately. (Problem: sensor_9)	7.63	183.12
222	4	8	100.00	MECHANICAL	LOW	\N	2026-04-30 02:47:49.052555	Machine operating normally (Problem: sensor_9)	159.10	3818.40
223	4	10	56.40	MECHANICAL	LOW	\N	2026-04-30 02:47:50.064205	Machine operating normally (Problem: sensor_9)	192.20	4612.80
224	4	11	51.20	MECHANICAL	LOW	\N	2026-04-30 02:47:50.544533	Machine operating normally (Problem: sensor_9)	46.53	1116.72
225	4	7	48.80	MECHANICAL	MEDIUM	\N	2026-04-30 02:47:51.155598	Warning: abnormal behavior detected near sensor_9. Schedule inspection. (Problem: sensor_9)	24.95	598.80
226	4	9	50.00	MECHANICAL	HIGH	\N	2026-04-30 02:47:51.669397	CRITICAL: inspect sensor_9 immediately. (Problem: sensor_9)	7.63	183.12
227	4	8	100.00	MECHANICAL	LOW	\N	2026-04-30 02:57:48.951033	Machine operating normally (Problem: sensor_9)	159.10	3818.40
228	4	10	56.40	MECHANICAL	LOW	\N	2026-04-30 02:57:49.546268	Machine operating normally (Problem: sensor_9)	192.20	4612.80
229	4	11	51.20	MECHANICAL	LOW	\N	2026-04-30 02:57:50.056254	Machine operating normally (Problem: sensor_9)	46.53	1116.72
230	4	7	48.80	MECHANICAL	MEDIUM	\N	2026-04-30 02:57:50.546572	Warning: abnormal behavior detected near sensor_9. Schedule inspection. (Problem: sensor_9)	24.95	598.80
231	4	9	50.00	MECHANICAL	HIGH	\N	2026-04-30 02:57:51.05361	CRITICAL: inspect sensor_9 immediately. (Problem: sensor_9)	7.63	183.12
232	4	8	100.00	MECHANICAL	LOW	\N	2026-04-30 03:07:48.961334	Machine operating normally (Problem: sensor_9)	159.10	3818.40
233	4	10	56.40	MECHANICAL	LOW	\N	2026-04-30 03:07:49.459931	Machine operating normally (Problem: sensor_9)	192.20	4612.80
234	4	11	51.20	MECHANICAL	LOW	\N	2026-04-30 03:07:50.050937	Machine operating normally (Problem: sensor_9)	46.53	1116.72
235	4	7	48.80	MECHANICAL	MEDIUM	\N	2026-04-30 03:07:50.556213	Warning: abnormal behavior detected near sensor_9. Schedule inspection. (Problem: sensor_9)	24.95	598.80
236	4	9	50.00	MECHANICAL	HIGH	\N	2026-04-30 03:07:51.172655	CRITICAL: inspect sensor_9 immediately. (Problem: sensor_9)	7.63	183.12
237	4	8	100.00	MECHANICAL	LOW	\N	2026-04-30 03:17:49.065245	Machine operating normally (Problem: sensor_9)	159.10	3818.40
238	4	10	56.40	MECHANICAL	LOW	\N	2026-04-30 03:17:49.664194	Machine operating normally (Problem: sensor_9)	192.20	4612.80
239	4	11	51.20	MECHANICAL	LOW	\N	2026-04-30 03:17:50.163276	Machine operating normally (Problem: sensor_9)	46.53	1116.72
240	4	7	48.80	MECHANICAL	MEDIUM	\N	2026-04-30 03:17:50.660558	Warning: abnormal behavior detected near sensor_9. Schedule inspection. (Problem: sensor_9)	24.95	598.80
241	4	9	50.00	MECHANICAL	HIGH	\N	2026-04-30 03:17:51.568182	CRITICAL: inspect sensor_9 immediately. (Problem: sensor_9)	7.63	183.12
242	4	8	100.00	MECHANICAL	LOW	\N	2026-04-30 03:27:49.082983	Machine operating normally (Problem: sensor_9)	159.10	3818.40
243	4	10	56.40	MECHANICAL	LOW	\N	2026-04-30 03:27:49.672207	Machine operating normally (Problem: sensor_9)	192.20	4612.80
244	4	11	51.20	MECHANICAL	LOW	\N	2026-04-30 03:27:50.277848	Machine operating normally (Problem: sensor_9)	46.53	1116.72
245	4	7	48.80	MECHANICAL	MEDIUM	\N	2026-04-30 03:27:50.766908	Warning: abnormal behavior detected near sensor_9. Schedule inspection. (Problem: sensor_9)	24.95	598.80
246	4	9	50.00	MECHANICAL	HIGH	\N	2026-04-30 03:27:51.907705	CRITICAL: inspect sensor_9 immediately. (Problem: sensor_9)	7.63	183.12
247	4	8	100.00	MECHANICAL	LOW	\N	2026-04-30 13:20:39.094934	Machine operating normally (Problem: sensor_9)	159.10	3818.40
248	4	10	56.40	MECHANICAL	LOW	\N	2026-04-30 13:20:40.595385	Machine operating normally (Problem: sensor_9)	192.20	4612.80
249	4	11	51.20	MECHANICAL	LOW	\N	2026-04-30 13:20:41.330026	Machine operating normally (Problem: sensor_9)	46.53	1116.72
250	4	7	48.80	MECHANICAL	MEDIUM	\N	2026-04-30 13:20:42.129023	Warning: abnormal behavior detected near sensor_9. Schedule inspection. (Problem: sensor_9)	24.95	598.80
251	4	9	50.00	MECHANICAL	HIGH	\N	2026-04-30 13:20:42.916887	CRITICAL: inspect sensor_9 immediately. (Problem: sensor_9)	7.63	183.12
252	4	8	100.00	MECHANICAL	LOW	\N	2026-04-30 17:11:36.679222	Machine operating normally (Problem: sensor_9)	159.10	3818.40
253	4	10	56.40	MECHANICAL	LOW	\N	2026-04-30 17:11:37.350292	Machine operating normally (Problem: sensor_9)	192.20	4612.80
254	4	11	51.20	MECHANICAL	LOW	\N	2026-04-30 17:11:37.955594	Machine operating normally (Problem: sensor_9)	46.53	1116.72
255	4	7	48.80	MECHANICAL	MEDIUM	\N	2026-04-30 17:11:38.548879	Warning: abnormal behavior detected near sensor_9. Schedule inspection. (Problem: sensor_9)	24.95	598.80
256	4	9	50.00	MECHANICAL	HIGH	\N	2026-04-30 17:11:39.250039	CRITICAL: inspect sensor_9 immediately. (Problem: sensor_9)	7.63	183.12
257	4	8	100.00	MECHANICAL	LOW	\N	2026-04-30 17:20:33.658023	Machine operating normally (Problem: sensor_9)	159.10	3818.40
258	4	10	56.40	MECHANICAL	LOW	\N	2026-04-30 17:20:34.170604	Machine operating normally (Problem: sensor_9)	192.20	4612.80
259	4	11	51.20	MECHANICAL	LOW	\N	2026-04-30 17:20:34.759706	Machine operating normally (Problem: sensor_9)	46.53	1116.72
260	4	7	48.80	MECHANICAL	MEDIUM	\N	2026-04-30 17:20:35.253906	Warning: abnormal behavior detected near sensor_9. Schedule inspection. (Problem: sensor_9)	24.95	598.80
261	4	9	50.00	MECHANICAL	HIGH	\N	2026-04-30 17:20:35.75165	CRITICAL: inspect sensor_9 immediately. (Problem: sensor_9)	7.63	183.12
262	4	8	100.00	MECHANICAL	LOW	\N	2026-04-30 17:30:34.906207	Machine operating normally (Problem: sensor_9)	159.10	3818.40
263	4	10	56.40	MECHANICAL	LOW	\N	2026-04-30 17:30:35.861775	Machine operating normally (Problem: sensor_9)	192.20	4612.80
264	4	11	51.20	MECHANICAL	LOW	\N	2026-04-30 17:30:36.489835	Machine operating normally (Problem: sensor_9)	46.53	1116.72
265	4	7	48.80	MECHANICAL	MEDIUM	\N	2026-04-30 17:30:37.35023	Warning: abnormal behavior detected near sensor_9. Schedule inspection. (Problem: sensor_9)	24.95	598.80
266	4	9	50.00	MECHANICAL	HIGH	\N	2026-04-30 17:30:38.128972	CRITICAL: inspect sensor_9 immediately. (Problem: sensor_9)	7.63	183.12
267	4	8	100.00	MECHANICAL	LOW	\N	2026-04-30 17:40:34.103259	Machine operating normally (Problem: sensor_9)	159.10	3818.40
268	4	10	56.40	MECHANICAL	LOW	\N	2026-04-30 17:40:34.76365	Machine operating normally (Problem: sensor_9)	192.20	4612.80
269	4	11	51.20	MECHANICAL	LOW	\N	2026-04-30 17:40:35.364867	Machine operating normally (Problem: sensor_9)	46.53	1116.72
270	4	7	48.80	MECHANICAL	MEDIUM	\N	2026-04-30 17:40:36.002931	Warning: abnormal behavior detected near sensor_9. Schedule inspection. (Problem: sensor_9)	24.95	598.80
271	4	9	50.00	MECHANICAL	HIGH	\N	2026-04-30 17:40:36.606047	CRITICAL: inspect sensor_9 immediately. (Problem: sensor_9)	7.63	183.12
272	4	8	100.00	MECHANICAL	LOW	\N	2026-04-30 17:50:33.676697	Machine operating normally (Problem: sensor_9)	159.10	3818.40
273	4	10	56.40	MECHANICAL	LOW	\N	2026-04-30 17:50:34.267954	Machine operating normally (Problem: sensor_9)	192.20	4612.80
274	4	11	51.20	MECHANICAL	LOW	\N	2026-04-30 17:50:34.889279	Machine operating normally (Problem: sensor_9)	46.53	1116.72
275	4	7	48.80	MECHANICAL	MEDIUM	\N	2026-04-30 17:50:35.483483	Warning: abnormal behavior detected near sensor_9. Schedule inspection. (Problem: sensor_9)	24.95	598.80
276	4	9	50.00	MECHANICAL	HIGH	\N	2026-04-30 17:50:36.08059	CRITICAL: inspect sensor_9 immediately. (Problem: sensor_9)	7.63	183.12
277	4	8	100.00	MECHANICAL	LOW	\N	2026-04-30 18:00:33.775749	Machine operating normally (Problem: sensor_9)	159.10	3818.40
278	4	10	56.40	MECHANICAL	LOW	\N	2026-04-30 18:00:34.379878	Machine operating normally (Problem: sensor_9)	192.20	4612.80
279	4	11	51.20	MECHANICAL	LOW	\N	2026-04-30 18:00:34.991915	Machine operating normally (Problem: sensor_9)	46.53	1116.72
280	4	7	48.80	MECHANICAL	MEDIUM	\N	2026-04-30 18:00:35.609814	Warning: abnormal behavior detected near sensor_9. Schedule inspection. (Problem: sensor_9)	24.95	598.80
281	4	9	50.00	MECHANICAL	HIGH	\N	2026-04-30 18:00:36.174431	CRITICAL: inspect sensor_9 immediately. (Problem: sensor_9)	7.63	183.12
282	4	8	100.00	MECHANICAL	LOW	\N	2026-04-30 18:41:37.283749	Machine operating normally (Problem: sensor_9)	159.10	3818.40
283	4	10	56.40	MECHANICAL	LOW	\N	2026-04-30 18:41:37.804395	Machine operating normally (Problem: sensor_9)	192.20	4612.80
284	4	11	51.20	MECHANICAL	LOW	\N	2026-04-30 18:41:38.354547	Machine operating normally (Problem: sensor_9)	46.53	1116.72
285	4	7	48.80	MECHANICAL	MEDIUM	\N	2026-04-30 18:41:38.966957	Warning: abnormal behavior detected near sensor_9. Schedule inspection. (Problem: sensor_9)	24.95	598.80
286	4	9	50.00	MECHANICAL	HIGH	\N	2026-04-30 18:41:39.6258	CRITICAL: inspect sensor_9 immediately. (Problem: sensor_9)	7.63	183.12
287	4	8	100.00	MECHANICAL	LOW	\N	2026-04-30 18:41:40.259766	Machine operating normally (Problem: sensor_9)	159.10	3818.40
288	4	10	56.40	MECHANICAL	LOW	\N	2026-04-30 18:41:40.945097	Machine operating normally (Problem: sensor_9)	192.20	4612.80
289	4	11	51.20	MECHANICAL	LOW	\N	2026-04-30 18:41:41.571567	Machine operating normally (Problem: sensor_9)	46.53	1116.72
290	4	7	48.80	MECHANICAL	MEDIUM	\N	2026-04-30 18:41:42.49702	Warning: abnormal behavior detected near sensor_9. Schedule inspection. (Problem: sensor_9)	24.95	598.80
291	4	9	50.00	MECHANICAL	HIGH	\N	2026-04-30 18:41:43.113848	CRITICAL: inspect sensor_9 immediately. (Problem: sensor_9)	7.63	183.12
292	4	8	100.00	MECHANICAL	LOW	\N	2026-04-30 18:41:43.760528	Machine operating normally (Problem: sensor_9)	159.10	3818.40
293	4	10	56.40	MECHANICAL	LOW	\N	2026-04-30 18:41:44.35955	Machine operating normally (Problem: sensor_9)	192.20	4612.80
294	4	11	51.20	MECHANICAL	LOW	\N	2026-04-30 18:41:44.964913	Machine operating normally (Problem: sensor_9)	46.53	1116.72
295	4	7	48.80	MECHANICAL	MEDIUM	\N	2026-04-30 18:41:45.56554	Warning: abnormal behavior detected near sensor_9. Schedule inspection. (Problem: sensor_9)	24.95	598.80
296	4	9	50.00	MECHANICAL	HIGH	\N	2026-04-30 18:41:46.15534	CRITICAL: inspect sensor_9 immediately. (Problem: sensor_9)	7.63	183.12
297	4	8	100.00	MECHANICAL	LOW	\N	2026-04-30 18:41:46.698411	Machine operating normally (Problem: sensor_9)	159.10	3818.40
298	4	10	56.40	MECHANICAL	LOW	\N	2026-04-30 18:41:47.291897	Machine operating normally (Problem: sensor_9)	192.20	4612.80
299	4	11	51.20	MECHANICAL	LOW	\N	2026-04-30 18:41:47.969111	Machine operating normally (Problem: sensor_9)	46.53	1116.72
300	4	7	48.80	MECHANICAL	MEDIUM	\N	2026-04-30 18:41:48.562467	Warning: abnormal behavior detected near sensor_9. Schedule inspection. (Problem: sensor_9)	24.95	598.80
301	4	9	50.00	MECHANICAL	HIGH	\N	2026-04-30 18:41:49.154926	CRITICAL: inspect sensor_9 immediately. (Problem: sensor_9)	7.63	183.12
302	4	8	100.00	MECHANICAL	LOW	\N	2026-04-30 18:50:33.755114	Machine operating normally (Problem: sensor_9)	159.10	3818.40
303	4	10	56.40	MECHANICAL	LOW	\N	2026-04-30 18:50:34.366142	Machine operating normally (Problem: sensor_9)	192.20	4612.80
304	4	11	51.20	MECHANICAL	LOW	\N	2026-04-30 18:50:34.867038	Machine operating normally (Problem: sensor_9)	46.53	1116.72
305	4	7	48.80	MECHANICAL	MEDIUM	\N	2026-04-30 18:50:35.870732	Warning: abnormal behavior detected near sensor_9. Schedule inspection. (Problem: sensor_9)	24.95	598.80
306	4	9	50.00	MECHANICAL	HIGH	\N	2026-04-30 18:50:36.370315	CRITICAL: inspect sensor_9 immediately. (Problem: sensor_9)	7.63	183.12
307	4	8	100.00	MECHANICAL	LOW	\N	2026-04-30 19:00:33.671264	Machine operating normally (Problem: sensor_9)	159.10	3818.40
308	4	10	56.40	MECHANICAL	LOW	\N	2026-04-30 19:00:34.170738	Machine operating normally (Problem: sensor_9)	192.20	4612.80
309	4	11	51.20	MECHANICAL	LOW	\N	2026-04-30 19:00:34.859759	Machine operating normally (Problem: sensor_9)	46.53	1116.72
310	4	7	48.80	MECHANICAL	MEDIUM	\N	2026-04-30 19:00:35.466772	Warning: abnormal behavior detected near sensor_9. Schedule inspection. (Problem: sensor_9)	24.95	598.80
311	4	9	50.00	MECHANICAL	HIGH	\N	2026-04-30 19:00:36.088429	CRITICAL: inspect sensor_9 immediately. (Problem: sensor_9)	7.63	183.12
312	4	8	100.00	MECHANICAL	LOW	\N	2026-04-30 19:10:33.872849	Machine operating normally (Problem: sensor_9)	159.10	3818.40
313	4	10	56.40	MECHANICAL	LOW	\N	2026-04-30 19:10:34.377991	Machine operating normally (Problem: sensor_9)	192.20	4612.80
314	4	11	51.20	MECHANICAL	LOW	\N	2026-04-30 19:10:34.968681	Machine operating normally (Problem: sensor_9)	46.53	1116.72
315	4	7	48.80	MECHANICAL	MEDIUM	\N	2026-04-30 19:10:35.482595	Warning: abnormal behavior detected near sensor_9. Schedule inspection. (Problem: sensor_9)	24.95	598.80
316	4	9	50.00	MECHANICAL	HIGH	\N	2026-04-30 19:10:35.983547	CRITICAL: inspect sensor_9 immediately. (Problem: sensor_9)	7.63	183.12
317	4	8	100.00	MECHANICAL	LOW	\N	2026-04-30 19:20:34.098817	Machine operating normally (Problem: sensor_9)	159.10	3818.40
318	4	10	56.40	MECHANICAL	LOW	\N	2026-04-30 19:20:34.884271	Machine operating normally (Problem: sensor_9)	192.20	4612.80
319	4	11	51.20	MECHANICAL	LOW	\N	2026-04-30 19:20:35.581737	Machine operating normally (Problem: sensor_9)	46.53	1116.72
320	4	7	48.80	MECHANICAL	MEDIUM	\N	2026-04-30 19:20:36.275973	Warning: abnormal behavior detected near sensor_9. Schedule inspection. (Problem: sensor_9)	24.95	598.80
321	4	9	50.00	MECHANICAL	HIGH	\N	2026-04-30 19:20:37.638969	CRITICAL: inspect sensor_9 immediately. (Problem: sensor_9)	7.63	183.12
322	4	8	100.00	MECHANICAL	LOW	\N	2026-04-30 19:30:33.786551	Machine operating normally (Problem: sensor_9)	159.10	3818.40
323	4	10	56.40	MECHANICAL	LOW	\N	2026-04-30 19:30:34.296221	Machine operating normally (Problem: sensor_9)	192.20	4612.80
324	4	11	51.20	MECHANICAL	LOW	\N	2026-04-30 19:30:34.790452	Machine operating normally (Problem: sensor_9)	46.53	1116.72
325	4	7	48.80	MECHANICAL	MEDIUM	\N	2026-04-30 19:30:35.284938	Warning: abnormal behavior detected near sensor_9. Schedule inspection. (Problem: sensor_9)	24.95	598.80
326	4	9	50.00	MECHANICAL	HIGH	\N	2026-04-30 19:30:35.976374	CRITICAL: inspect sensor_9 immediately. (Problem: sensor_9)	7.63	183.12
327	4	8	100.00	MECHANICAL	LOW	\N	2026-04-30 19:40:33.782135	Machine operating normally (Problem: sensor_9)	159.10	3818.40
328	4	10	56.40	MECHANICAL	LOW	\N	2026-04-30 19:40:34.287589	Machine operating normally (Problem: sensor_9)	192.20	4612.80
329	4	11	51.20	MECHANICAL	LOW	\N	2026-04-30 19:40:35.318271	Machine operating normally (Problem: sensor_9)	46.53	1116.72
330	4	7	48.80	MECHANICAL	MEDIUM	\N	2026-04-30 19:40:35.91085	Warning: abnormal behavior detected near sensor_9. Schedule inspection. (Problem: sensor_9)	24.95	598.80
331	4	9	50.00	MECHANICAL	HIGH	\N	2026-04-30 19:40:36.39846	CRITICAL: inspect sensor_9 immediately. (Problem: sensor_9)	7.63	183.12
332	4	8	100.00	MECHANICAL	LOW	\N	2026-04-30 19:50:33.802736	Machine operating normally (Problem: sensor_9)	159.10	3818.40
333	4	10	56.40	MECHANICAL	LOW	\N	2026-04-30 19:50:34.39345	Machine operating normally (Problem: sensor_9)	192.20	4612.80
334	4	11	51.20	MECHANICAL	LOW	\N	2026-04-30 19:50:34.8949	Machine operating normally (Problem: sensor_9)	46.53	1116.72
335	4	7	48.80	MECHANICAL	MEDIUM	\N	2026-04-30 19:50:35.492814	Warning: abnormal behavior detected near sensor_9. Schedule inspection. (Problem: sensor_9)	24.95	598.80
336	4	9	50.00	MECHANICAL	HIGH	\N	2026-04-30 19:50:35.99766	CRITICAL: inspect sensor_9 immediately. (Problem: sensor_9)	7.63	183.12
337	4	8	100.00	MECHANICAL	LOW	\N	2026-04-30 20:00:33.807227	Machine operating normally (Problem: sensor_9)	159.10	3818.40
338	4	10	56.40	MECHANICAL	LOW	\N	2026-04-30 20:00:34.427892	Machine operating normally (Problem: sensor_9)	192.20	4612.80
339	4	11	51.20	MECHANICAL	LOW	\N	2026-04-30 20:00:35.059054	Machine operating normally (Problem: sensor_9)	46.53	1116.72
340	4	7	48.80	MECHANICAL	MEDIUM	\N	2026-04-30 20:00:35.638113	Warning: abnormal behavior detected near sensor_9. Schedule inspection. (Problem: sensor_9)	24.95	598.80
341	4	9	50.00	MECHANICAL	HIGH	\N	2026-04-30 20:00:36.622816	CRITICAL: inspect sensor_9 immediately. (Problem: sensor_9)	7.63	183.12
342	4	8	100.00	MECHANICAL	LOW	\N	2026-04-30 20:10:33.622925	Machine operating normally (Problem: sensor_9)	159.10	3818.40
343	4	10	56.40	MECHANICAL	LOW	\N	2026-04-30 20:10:34.107944	Machine operating normally (Problem: sensor_9)	192.20	4612.80
344	4	11	51.20	MECHANICAL	LOW	\N	2026-04-30 20:10:35.031916	Machine operating normally (Problem: sensor_9)	46.53	1116.72
345	4	7	48.80	MECHANICAL	MEDIUM	\N	2026-04-30 20:10:35.503183	Warning: abnormal behavior detected near sensor_9. Schedule inspection. (Problem: sensor_9)	24.95	598.80
346	4	9	50.00	MECHANICAL	HIGH	\N	2026-04-30 20:10:36.007207	CRITICAL: inspect sensor_9 immediately. (Problem: sensor_9)	7.63	183.12
347	4	8	100.00	MECHANICAL	LOW	\N	2026-04-30 20:20:34.421406	Machine operating normally (Problem: sensor_9)	159.10	3818.40
348	4	10	56.40	MECHANICAL	LOW	\N	2026-04-30 20:20:35.107141	Machine operating normally (Problem: sensor_9)	192.20	4612.80
349	4	11	51.20	MECHANICAL	LOW	\N	2026-04-30 20:20:35.711157	Machine operating normally (Problem: sensor_9)	46.53	1116.72
350	4	7	48.80	MECHANICAL	MEDIUM	\N	2026-04-30 20:20:36.348384	Warning: abnormal behavior detected near sensor_9. Schedule inspection. (Problem: sensor_9)	24.95	598.80
351	4	9	50.00	MECHANICAL	HIGH	\N	2026-04-30 20:20:37.007906	CRITICAL: inspect sensor_9 immediately. (Problem: sensor_9)	7.63	183.12
352	4	8	100.00	MECHANICAL	LOW	\N	2026-04-30 20:30:33.617709	Machine operating normally (Problem: sensor_9)	159.10	3818.40
353	4	10	56.40	MECHANICAL	LOW	\N	2026-04-30 20:30:34.217603	Machine operating normally (Problem: sensor_9)	192.20	4612.80
354	4	11	51.20	MECHANICAL	LOW	\N	2026-04-30 20:30:34.718746	Machine operating normally (Problem: sensor_9)	46.53	1116.72
355	4	7	48.80	MECHANICAL	MEDIUM	\N	2026-04-30 20:30:35.411886	Warning: abnormal behavior detected near sensor_9. Schedule inspection. (Problem: sensor_9)	24.95	598.80
356	4	9	50.00	MECHANICAL	HIGH	\N	2026-04-30 20:30:36.007769	CRITICAL: inspect sensor_9 immediately. (Problem: sensor_9)	7.63	183.12
357	4	8	100.00	MECHANICAL	LOW	\N	2026-04-30 20:40:33.625609	Machine operating normally (Problem: sensor_9)	159.10	3818.40
358	4	10	56.40	MECHANICAL	LOW	\N	2026-04-30 20:40:34.124473	Machine operating normally (Problem: sensor_9)	192.20	4612.80
359	4	11	51.20	MECHANICAL	LOW	\N	2026-04-30 20:40:34.628078	Machine operating normally (Problem: sensor_9)	46.53	1116.72
360	4	7	48.80	MECHANICAL	MEDIUM	\N	2026-04-30 20:40:35.118623	Warning: abnormal behavior detected near sensor_9. Schedule inspection. (Problem: sensor_9)	24.95	598.80
361	4	9	50.00	MECHANICAL	HIGH	\N	2026-04-30 20:40:35.625457	CRITICAL: inspect sensor_9 immediately. (Problem: sensor_9)	7.63	183.12
362	4	8	100.00	MECHANICAL	LOW	\N	2026-04-30 20:50:33.733458	Machine operating normally (Problem: sensor_9)	159.10	3818.40
363	4	10	56.40	MECHANICAL	LOW	\N	2026-04-30 20:50:34.326291	Machine operating normally (Problem: sensor_9)	192.20	4612.80
364	4	11	51.20	MECHANICAL	LOW	\N	2026-04-30 20:50:34.830072	Machine operating normally (Problem: sensor_9)	46.53	1116.72
365	4	7	48.80	MECHANICAL	MEDIUM	\N	2026-04-30 20:50:35.742542	Warning: abnormal behavior detected near sensor_9. Schedule inspection. (Problem: sensor_9)	24.95	598.80
366	4	9	50.00	MECHANICAL	HIGH	\N	2026-04-30 20:50:36.329733	CRITICAL: inspect sensor_9 immediately. (Problem: sensor_9)	7.63	183.12
367	4	8	100.00	MECHANICAL	LOW	\N	2026-04-30 21:00:33.828261	Machine operating normally (Problem: sensor_9)	159.10	3818.40
368	4	10	56.40	MECHANICAL	LOW	\N	2026-04-30 21:00:34.861664	Machine operating normally (Problem: sensor_9)	192.20	4612.80
369	4	11	51.20	MECHANICAL	LOW	\N	2026-04-30 21:00:35.432016	Machine operating normally (Problem: sensor_9)	46.53	1116.72
370	4	7	48.80	MECHANICAL	MEDIUM	\N	2026-04-30 21:00:36.128998	Warning: abnormal behavior detected near sensor_9. Schedule inspection. (Problem: sensor_9)	24.95	598.80
371	4	9	50.00	MECHANICAL	HIGH	\N	2026-04-30 21:00:36.744086	CRITICAL: inspect sensor_9 immediately. (Problem: sensor_9)	7.63	183.12
372	4	8	100.00	MECHANICAL	LOW	\N	2026-04-30 21:10:33.742403	Machine operating normally (Problem: sensor_9)	159.10	3818.40
373	4	10	56.40	MECHANICAL	LOW	\N	2026-04-30 21:10:34.241693	Machine operating normally (Problem: sensor_9)	192.20	4612.80
374	4	11	51.20	MECHANICAL	LOW	\N	2026-04-30 21:10:34.739843	Machine operating normally (Problem: sensor_9)	46.53	1116.72
375	4	7	48.80	MECHANICAL	MEDIUM	\N	2026-04-30 21:10:35.236467	Warning: abnormal behavior detected near sensor_9. Schedule inspection. (Problem: sensor_9)	24.95	598.80
376	4	9	50.00	MECHANICAL	HIGH	\N	2026-04-30 21:10:35.738321	CRITICAL: inspect sensor_9 immediately. (Problem: sensor_9)	7.63	183.12
377	4	8	100.00	MECHANICAL	LOW	\N	2026-04-30 21:20:33.754165	Machine operating normally (Problem: sensor_9)	159.10	3818.40
378	4	10	56.40	MECHANICAL	LOW	\N	2026-04-30 21:20:34.347433	Machine operating normally (Problem: sensor_9)	192.20	4612.80
379	4	11	51.20	MECHANICAL	LOW	\N	2026-04-30 21:20:35.370258	Machine operating normally (Problem: sensor_9)	46.53	1116.72
380	4	7	48.80	MECHANICAL	MEDIUM	\N	2026-04-30 21:20:35.945199	Warning: abnormal behavior detected near sensor_9. Schedule inspection. (Problem: sensor_9)	24.95	598.80
381	4	9	50.00	MECHANICAL	HIGH	\N	2026-04-30 21:20:36.651397	CRITICAL: inspect sensor_9 immediately. (Problem: sensor_9)	7.63	183.12
382	4	8	100.00	MECHANICAL	LOW	\N	2026-04-30 21:30:34.017822	Machine operating normally (Problem: sensor_9)	159.10	3818.40
383	4	10	56.40	MECHANICAL	LOW	\N	2026-04-30 21:30:34.69132	Machine operating normally (Problem: sensor_9)	192.20	4612.80
384	4	11	51.20	MECHANICAL	LOW	\N	2026-04-30 21:30:35.244888	Machine operating normally (Problem: sensor_9)	46.53	1116.72
385	4	7	48.80	MECHANICAL	MEDIUM	\N	2026-04-30 21:30:35.750279	Warning: abnormal behavior detected near sensor_9. Schedule inspection. (Problem: sensor_9)	24.95	598.80
386	4	9	50.00	MECHANICAL	HIGH	\N	2026-04-30 21:30:36.369171	CRITICAL: inspect sensor_9 immediately. (Problem: sensor_9)	7.63	183.12
387	4	8	100.00	MECHANICAL	LOW	\N	2026-04-30 21:54:21.049906	Machine operating normally (Problem: sensor_9)	159.10	3818.40
388	4	10	56.40	MECHANICAL	LOW	\N	2026-04-30 21:54:21.550865	Machine operating normally (Problem: sensor_9)	192.20	4612.80
389	4	11	51.20	MECHANICAL	LOW	\N	2026-04-30 21:54:22.059012	Machine operating normally (Problem: sensor_9)	46.53	1116.72
390	4	7	48.80	MECHANICAL	MEDIUM	\N	2026-04-30 21:54:22.55551	Warning: abnormal behavior detected near sensor_9. Schedule inspection. (Problem: sensor_9)	24.95	598.80
391	4	9	50.00	MECHANICAL	HIGH	\N	2026-04-30 21:54:23.052722	CRITICAL: inspect sensor_9 immediately. (Problem: sensor_9)	7.63	183.12
392	4	8	100.00	MECHANICAL	LOW	\N	2026-04-30 21:54:23.559601	Machine operating normally (Problem: sensor_9)	159.10	3818.40
393	4	10	56.40	MECHANICAL	LOW	\N	2026-04-30 21:54:24.096391	Machine operating normally (Problem: sensor_9)	192.20	4612.80
394	4	11	51.20	MECHANICAL	LOW	\N	2026-04-30 21:54:24.550324	Machine operating normally (Problem: sensor_9)	46.53	1116.72
395	4	7	48.80	MECHANICAL	MEDIUM	\N	2026-04-30 21:54:25.057472	Warning: abnormal behavior detected near sensor_9. Schedule inspection. (Problem: sensor_9)	24.95	598.80
396	4	9	50.00	MECHANICAL	HIGH	\N	2026-04-30 21:54:25.73727	CRITICAL: inspect sensor_9 immediately. (Problem: sensor_9)	7.63	183.12
397	4	8	100.00	MECHANICAL	LOW	\N	2026-04-30 22:00:33.770845	Machine operating normally (Problem: sensor_9)	159.10	3818.40
398	4	10	56.40	MECHANICAL	LOW	\N	2026-04-30 22:00:34.264179	Machine operating normally (Problem: sensor_9)	192.20	4612.80
399	4	11	51.20	MECHANICAL	LOW	\N	2026-04-30 22:00:34.811817	Machine operating normally (Problem: sensor_9)	46.53	1116.72
400	4	7	48.80	MECHANICAL	MEDIUM	\N	2026-04-30 22:00:35.355478	Warning: abnormal behavior detected near sensor_9. Schedule inspection. (Problem: sensor_9)	24.95	598.80
401	4	9	50.00	MECHANICAL	HIGH	\N	2026-04-30 22:00:35.948004	CRITICAL: inspect sensor_9 immediately. (Problem: sensor_9)	7.63	183.12
\.


--
-- Data for Name: sensor; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.sensor (id, organization_id, machine_id, sensor_type_id, external_ref, status, current_value, last_reading_at, created_at) FROM stdin;
1	1	1	1	\N	ONLINE	75.00	\N	2026-04-21 02:33:36.02981
2	1	1	2	\N	ONLINE	0.30	\N	2026-04-21 02:33:36.02981
3	1	1	3	\N	ONLINE	95.00	\N	2026-04-21 02:33:36.02981
4	4	7	1	\N	ONLINE	78.50	2026-04-23 04:40:08.887439	2026-04-23 04:40:08.887439
5	4	7	2	\N	ONLINE	2.30	2026-04-23 04:40:08.887439	2026-04-23 04:40:08.887439
6	4	7	3	\N	ONLINE	85.00	2026-04-23 04:40:08.887439	2026-04-23 04:40:08.887439
7	4	8	1	\N	ONLINE	65.00	2026-04-23 04:40:08.887439	2026-04-23 04:40:08.887439
8	4	8	2	\N	ONLINE	1.10	2026-04-23 04:40:08.887439	2026-04-23 04:40:08.887439
9	4	9	1	\N	ONLINE	92.00	2026-04-23 04:40:08.887439	2026-04-23 04:40:08.887439
10	4	9	3	\N	ONLINE	98.00	2026-04-23 04:40:08.887439	2026-04-23 04:40:08.887439
11	4	10	1	\N	ONLINE	55.00	2026-04-23 04:40:08.887439	2026-04-23 04:40:08.887439
12	4	10	2	\N	ONLINE	0.80	2026-04-23 04:40:08.887439	2026-04-23 04:40:08.887439
13	4	11	1	\N	ONLINE	81.00	2026-04-23 04:40:08.887439	2026-04-23 04:40:08.887439
14	4	11	2	\N	ONLINE	3.10	2026-04-23 04:40:08.887439	2026-04-23 04:40:08.887439
17	4	7	4	\N	ONLINE	532.11	2026-04-30 00:43:19.365697	2026-04-30 00:43:19.365697
22	4	7	5	\N	ONLINE	651.72	2026-04-30 00:43:19.365697	2026-04-30 00:43:19.365697
27	4	7	6	\N	ONLINE	1602.41	2026-04-30 00:43:19.365697	2026-04-30 00:43:19.365697
32	4	7	7	\N	ONLINE	1412.88	2026-04-30 00:43:19.365697	2026-04-30 00:43:19.365697
37	4	7	8	\N	ONLINE	16.95	2026-04-30 00:43:19.365697	2026-04-30 00:43:19.365697
42	4	7	9	\N	ONLINE	24.17	2026-04-30 00:43:19.365697	2026-04-30 00:43:19.365697
47	4	7	10	\N	ONLINE	565.42	2026-04-30 00:43:19.365697	2026-04-30 00:43:19.365697
52	4	7	11	\N	ONLINE	2395.10	2026-04-30 00:43:19.365697	2026-04-30 00:43:19.365697
57	4	7	12	\N	ONLINE	9180.33	2026-04-30 00:43:19.365697	2026-04-30 00:43:19.365697
62	4	7	13	\N	ONLINE	1.36	2026-04-30 00:43:19.365697	2026-04-30 00:43:19.365697
67	4	7	14	\N	ONLINE	51.84	2026-04-30 00:43:19.365697	2026-04-30 00:43:19.365697
72	4	7	15	\N	ONLINE	535.62	2026-04-30 00:43:19.365697	2026-04-30 00:43:19.365697
77	4	7	16	\N	ONLINE	2392.40	2026-04-30 00:43:19.365697	2026-04-30 00:43:19.365697
19	4	11	4	\N	ONLINE	530.00	2026-04-30 00:43:19.365697	2026-04-30 00:43:19.365697
18	4	9	4	\N	ONLINE	642.12	2026-04-30 00:43:19.365697	2026-04-30 00:43:19.365697
23	4	9	5	\N	ONLINE	721.45	2026-04-30 00:43:19.365697	2026-04-30 00:43:19.365697
28	4	9	6	\N	ONLINE	1890.33	2026-04-30 00:43:19.365697	2026-04-30 00:43:19.365697
33	4	9	7	\N	ONLINE	1610.88	2026-04-30 00:43:19.365697	2026-04-30 00:43:19.365697
38	4	9	8	\N	ONLINE	32.41	2026-04-30 00:43:19.365697	2026-04-30 00:43:19.365697
43	4	9	9	\N	ONLINE	48.92	2026-04-30 00:43:19.365697	2026-04-30 00:43:19.365697
48	4	9	10	\N	ONLINE	710.55	2026-04-30 00:43:19.365697	2026-04-30 00:43:19.365697
53	4	9	11	\N	ONLINE	2499.73	2026-04-30 00:43:19.365697	2026-04-30 00:43:19.365697
58	4	9	12	\N	ONLINE	10450.66	2026-04-30 00:43:19.365697	2026-04-30 00:43:19.365697
63	4	9	13	\N	ONLINE	1.98	2026-04-30 00:43:19.365697	2026-04-30 00:43:19.365697
68	4	9	14	\N	ONLINE	89.44	2026-04-30 00:43:19.365697	2026-04-30 00:43:19.365697
73	4	9	15	\N	ONLINE	640.21	2026-04-30 00:43:19.365697	2026-04-30 00:43:19.365697
78	4	9	16	\N	ONLINE	2490.17	2026-04-30 00:43:19.365697	2026-04-30 00:43:19.365697
83	4	9	17	\N	ONLINE	9200.74	2026-04-30 00:43:19.365697	2026-04-30 00:43:19.365697
88	4	9	18	\N	ONLINE	11.55	2026-04-30 00:43:19.365697	2026-04-30 00:43:19.365697
93	4	9	19	\N	ONLINE	0.12	2026-04-30 00:43:19.365697	2026-04-30 00:43:19.365697
98	4	9	20	\N	ONLINE	510.00	2026-04-30 00:43:19.365697	2026-04-30 00:43:19.365697
103	4	9	21	\N	ONLINE	2510.00	2026-04-30 00:43:19.365697	2026-04-30 00:43:19.365697
108	4	9	22	\N	ONLINE	85.21	2026-04-30 00:43:19.365697	2026-04-30 00:43:19.365697
113	4	9	23	\N	ONLINE	52.90	2026-04-30 00:43:19.365697	2026-04-30 00:43:19.365697
118	4	9	24	\N	ONLINE	41.77	2026-04-30 00:43:19.365697	2026-04-30 00:43:19.365697
82	4	7	17	\N	ONLINE	8205.11	2026-04-30 00:43:19.365697	2026-04-30 00:43:19.365697
87	4	7	18	\N	ONLINE	8.61	2026-04-30 00:43:19.365697	2026-04-30 00:43:19.365697
92	4	7	19	\N	ONLINE	0.04	2026-04-30 00:43:19.365697	2026-04-30 00:43:19.365697
97	4	7	20	\N	ONLINE	401.00	2026-04-30 00:43:19.365697	2026-04-30 00:43:19.365697
102	4	7	21	\N	ONLINE	2391.00	2026-04-30 00:43:19.365697	2026-04-30 00:43:19.365697
107	4	7	22	\N	ONLINE	98.11	2026-04-30 00:43:19.365697	2026-04-30 00:43:19.365697
112	4	7	23	\N	ONLINE	40.25	2026-04-30 00:43:19.365697	2026-04-30 00:43:19.365697
117	4	7	24	\N	ONLINE	24.91	2026-04-30 00:43:19.365697	2026-04-30 00:43:19.365697
24	4	11	5	\N	ONLINE	648.00	2026-04-30 00:43:19.365697	2026-04-30 00:43:19.365697
29	4	11	6	\N	ONLINE	1598.00	2026-04-30 00:43:19.365697	2026-04-30 00:43:19.365697
34	4	11	7	\N	ONLINE	1410.00	2026-04-30 00:43:19.365697	2026-04-30 00:43:19.365697
39	4	11	8	\N	ONLINE	15.80	2026-04-30 00:43:19.365697	2026-04-30 00:43:19.365697
44	4	11	9	\N	ONLINE	23.50	2026-04-30 00:43:19.365697	2026-04-30 00:43:19.365697
49	4	11	10	\N	ONLINE	560.00	2026-04-30 00:43:19.365697	2026-04-30 00:43:19.365697
54	4	11	11	\N	ONLINE	2390.00	2026-04-30 00:43:19.365697	2026-04-30 00:43:19.365697
59	4	11	12	\N	ONLINE	9100.00	2026-04-30 00:43:19.365697	2026-04-30 00:43:19.365697
64	4	11	13	\N	ONLINE	1.33	2026-04-30 00:43:19.365697	2026-04-30 00:43:19.365697
69	4	11	14	\N	ONLINE	50.00	2026-04-30 00:43:19.365697	2026-04-30 00:43:19.365697
74	4	11	15	\N	ONLINE	530.00	2026-04-30 00:43:19.365697	2026-04-30 00:43:19.365697
79	4	11	16	\N	ONLINE	2388.00	2026-04-30 00:43:19.365697	2026-04-30 00:43:19.365697
84	4	11	17	\N	ONLINE	8150.00	2026-04-30 00:43:19.365697	2026-04-30 00:43:19.365697
89	4	11	18	\N	ONLINE	8.50	2026-04-30 00:43:19.365697	2026-04-30 00:43:19.365697
94	4	11	19	\N	ONLINE	0.04	2026-04-30 00:43:19.365697	2026-04-30 00:43:19.365697
99	4	11	20	\N	ONLINE	398.00	2026-04-30 00:43:19.365697	2026-04-30 00:43:19.365697
104	4	11	21	\N	ONLINE	2389.00	2026-04-30 00:43:19.365697	2026-04-30 00:43:19.365697
109	4	11	22	\N	ONLINE	97.00	2026-04-30 00:43:19.365697	2026-04-30 00:43:19.365697
114	4	11	23	\N	ONLINE	40.00	2026-04-30 00:43:19.365697	2026-04-30 00:43:19.365697
119	4	11	24	\N	ONLINE	24.50	2026-04-30 00:43:19.365697	2026-04-30 00:43:19.365697
15	4	8	4	\N	ONLINE	518.67	2026-04-30 00:43:19.365697	2026-04-30 00:43:19.365697
20	4	8	5	\N	ONLINE	643.02	2026-04-30 00:43:19.365697	2026-04-30 00:43:19.365697
25	4	8	6	\N	ONLINE	1585.29	2026-04-30 00:43:19.365697	2026-04-30 00:43:19.365697
30	4	8	7	\N	ONLINE	1398.21	2026-04-30 00:43:19.365697	2026-04-30 00:43:19.365697
35	4	8	8	\N	ONLINE	14.62	2026-04-30 00:43:19.365697	2026-04-30 00:43:19.365697
40	4	8	9	\N	ONLINE	21.61	2026-04-30 00:43:19.365697	2026-04-30 00:43:19.365697
45	4	8	10	\N	ONLINE	554.36	2026-04-30 00:43:19.365697	2026-04-30 00:43:19.365697
50	4	8	11	\N	ONLINE	2388.06	2026-04-30 00:43:19.365697	2026-04-30 00:43:19.365697
55	4	8	12	\N	ONLINE	9046.19	2026-04-30 00:43:19.365697	2026-04-30 00:43:19.365697
60	4	8	13	\N	ONLINE	1.30	2026-04-30 00:43:19.365697	2026-04-30 00:43:19.365697
65	4	8	14	\N	ONLINE	47.47	2026-04-30 00:43:19.365697	2026-04-30 00:43:19.365697
70	4	8	15	\N	ONLINE	521.66	2026-04-30 00:43:19.365697	2026-04-30 00:43:19.365697
75	4	8	16	\N	ONLINE	2388.02	2026-04-30 00:43:19.365697	2026-04-30 00:43:19.365697
80	4	8	17	\N	ONLINE	8138.62	2026-04-30 00:43:19.365697	2026-04-30 00:43:19.365697
85	4	8	18	\N	ONLINE	8.42	2026-04-30 00:43:19.365697	2026-04-30 00:43:19.365697
90	4	8	19	\N	ONLINE	0.03	2026-04-30 00:43:19.365697	2026-04-30 00:43:19.365697
95	4	8	20	\N	ONLINE	392.00	2026-04-30 00:43:19.365697	2026-04-30 00:43:19.365697
100	4	8	21	\N	ONLINE	2388.00	2026-04-30 00:43:19.365697	2026-04-30 00:43:19.365697
105	4	8	22	\N	ONLINE	100.00	2026-04-30 00:43:19.365697	2026-04-30 00:43:19.365697
110	4	8	23	\N	ONLINE	39.06	2026-04-30 00:43:19.365697	2026-04-30 00:43:19.365697
115	4	8	24	\N	ONLINE	23.42	2026-04-30 00:43:19.365697	2026-04-30 00:43:19.365697
16	4	10	4	\N	ONLINE	515.00	2026-04-30 00:43:19.365697	2026-04-30 00:43:19.365697
21	4	10	5	\N	ONLINE	640.00	2026-04-30 00:43:19.365697	2026-04-30 00:43:19.365697
26	4	10	6	\N	ONLINE	1580.00	2026-04-30 00:43:19.365697	2026-04-30 00:43:19.365697
31	4	10	7	\N	ONLINE	1395.00	2026-04-30 00:43:19.365697	2026-04-30 00:43:19.365697
36	4	10	8	\N	ONLINE	14.00	2026-04-30 00:43:19.365697	2026-04-30 00:43:19.365697
41	4	10	9	\N	ONLINE	21.00	2026-04-30 00:43:19.365697	2026-04-30 00:43:19.365697
46	4	10	10	\N	ONLINE	550.00	2026-04-30 00:43:19.365697	2026-04-30 00:43:19.365697
51	4	10	11	\N	ONLINE	2385.00	2026-04-30 00:43:19.365697	2026-04-30 00:43:19.365697
56	4	10	12	\N	ONLINE	9000.00	2026-04-30 00:43:19.365697	2026-04-30 00:43:19.365697
61	4	10	13	\N	ONLINE	1.28	2026-04-30 00:43:19.365697	2026-04-30 00:43:19.365697
66	4	10	14	\N	ONLINE	46.00	2026-04-30 00:43:19.365697	2026-04-30 00:43:19.365697
71	4	10	15	\N	ONLINE	518.00	2026-04-30 00:43:19.365697	2026-04-30 00:43:19.365697
76	4	10	16	\N	ONLINE	2385.00	2026-04-30 00:43:19.365697	2026-04-30 00:43:19.365697
81	4	10	17	\N	ONLINE	8100.00	2026-04-30 00:43:19.365697	2026-04-30 00:43:19.365697
86	4	10	18	\N	ONLINE	8.30	2026-04-30 00:43:19.365697	2026-04-30 00:43:19.365697
91	4	10	19	\N	ONLINE	0.03	2026-04-30 00:43:19.365697	2026-04-30 00:43:19.365697
96	4	10	20	\N	ONLINE	388.00	2026-04-30 00:43:19.365697	2026-04-30 00:43:19.365697
101	4	10	21	\N	ONLINE	2385.00	2026-04-30 00:43:19.365697	2026-04-30 00:43:19.365697
106	4	10	22	\N	ONLINE	100.00	2026-04-30 00:43:19.365697	2026-04-30 00:43:19.365697
111	4	10	23	\N	ONLINE	38.50	2026-04-30 00:43:19.365697	2026-04-30 00:43:19.365697
116	4	10	24	\N	ONLINE	23.00	2026-04-30 00:43:19.365697	2026-04-30 00:43:19.365697
\.


--
-- Data for Name: sensor_reading; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.sensor_reading (id, sensor_id, value, reading_time, quality, ingested_at) FROM stdin;
1	1	72.5	2026-04-20 21:33:59.167826	VALID	2026-04-21 02:33:59.167826
2	1	73.2	2026-04-20 22:33:59.167826	VALID	2026-04-21 02:33:59.167826
3	1	74.1	2026-04-20 23:33:59.167826	VALID	2026-04-21 02:33:59.167826
4	1	75	2026-04-21 00:33:59.167826	VALID	2026-04-21 02:33:59.167826
5	2	0.2	2026-04-20 21:33:59.167826	VALID	2026-04-21 02:33:59.167826
6	2	0.25	2026-04-20 22:33:59.167826	VALID	2026-04-21 02:33:59.167826
7	2	0.28	2026-04-20 23:33:59.167826	VALID	2026-04-21 02:33:59.167826
8	2	0.3	2026-04-21 00:33:59.167826	VALID	2026-04-21 02:33:59.167826
9	3	90	2026-04-20 21:33:59.167826	VALID	2026-04-21 02:33:59.167826
10	3	92	2026-04-20 22:33:59.167826	VALID	2026-04-21 02:33:59.167826
11	3	94	2026-04-20 23:33:59.167826	VALID	2026-04-21 02:33:59.167826
12	3	95	2026-04-21 00:33:59.167826	VALID	2026-04-21 02:33:59.167826
13	4	93.06965076653941	2026-04-23 03:43:13.243213	VALID	2026-04-23 04:43:13.243213
14	4	79.12096431442014	2026-04-23 02:43:13.243213	VALID	2026-04-23 04:43:13.243213
15	4	81.12028423867366	2026-04-23 01:43:13.243213	VALID	2026-04-23 04:43:13.243213
16	4	70.5436659968894	2026-04-23 00:43:13.243213	VALID	2026-04-23 04:43:13.243213
17	4	91.74185323830953	2026-04-22 23:43:13.243213	VALID	2026-04-23 04:43:13.243213
18	4	74.6713380038136	2026-04-22 22:43:13.243213	VALID	2026-04-23 04:43:13.243213
19	4	75.26502976920013	2026-04-22 21:43:13.243213	VALID	2026-04-23 04:43:13.243213
20	4	76.73989104796705	2026-04-22 20:43:13.243213	VALID	2026-04-23 04:43:13.243213
21	4	73.22705892222645	2026-04-22 19:43:13.243213	VALID	2026-04-23 04:43:13.243213
22	4	74.394236816224	2026-04-22 18:43:13.243213	VALID	2026-04-23 04:43:13.243213
23	4	91.71039963072754	2026-04-22 17:43:13.243213	VALID	2026-04-23 04:43:13.243213
24	4	84.84453266037369	2026-04-22 16:43:13.243213	VALID	2026-04-23 04:43:13.243213
25	4	77.63274057591941	2026-04-22 15:43:13.243213	VALID	2026-04-23 04:43:13.243213
26	4	74.00431124337251	2026-04-22 14:43:13.243213	VALID	2026-04-23 04:43:13.243213
27	4	89.18965368160282	2026-04-22 13:43:13.243213	VALID	2026-04-23 04:43:13.243213
28	4	71.29819714645627	2026-04-22 12:43:13.243213	VALID	2026-04-23 04:43:13.243213
29	4	86.73354999714282	2026-04-22 11:43:13.243213	VALID	2026-04-23 04:43:13.243213
30	4	90.8558820543457	2026-04-22 10:43:13.243213	VALID	2026-04-23 04:43:13.243213
31	4	77.16766054962308	2026-04-22 09:43:13.243213	VALID	2026-04-23 04:43:13.243213
32	4	79.61031727608717	2026-04-22 08:43:13.243213	VALID	2026-04-23 04:43:13.243213
33	4	89.7696653055844	2026-04-22 07:43:13.243213	VALID	2026-04-23 04:43:13.243213
34	4	91.05167583601046	2026-04-22 06:43:13.243213	VALID	2026-04-23 04:43:13.243213
35	4	89.61717543287787	2026-04-22 05:43:13.243213	VALID	2026-04-23 04:43:13.243213
36	4	83.05512876704837	2026-04-22 04:43:13.243213	VALID	2026-04-23 04:43:13.243213
37	5	3.906490978029593	2026-04-23 03:43:13.243213	VALID	2026-04-23 04:43:13.243213
38	5	0.7553914440627214	2026-04-23 02:43:13.243213	VALID	2026-04-23 04:43:13.243213
39	5	1.6922744166854757	2026-04-23 01:43:13.243213	VALID	2026-04-23 04:43:13.243213
40	5	1.1446576047150026	2026-04-23 00:43:13.243213	VALID	2026-04-23 04:43:13.243213
41	5	1.4614284363439773	2026-04-22 23:43:13.243213	VALID	2026-04-23 04:43:13.243213
42	5	3.0126894529850565	2026-04-22 22:43:13.243213	VALID	2026-04-23 04:43:13.243213
43	5	3.456102131778208	2026-04-22 21:43:13.243213	VALID	2026-04-23 04:43:13.243213
44	5	1.1259889254633113	2026-04-22 20:43:13.243213	VALID	2026-04-23 04:43:13.243213
45	5	1.414463860530464	2026-04-22 19:43:13.243213	VALID	2026-04-23 04:43:13.243213
46	5	3.635064180130236	2026-04-22 18:43:13.243213	VALID	2026-04-23 04:43:13.243213
47	5	3.6633497746415102	2026-04-22 17:43:13.243213	VALID	2026-04-23 04:43:13.243213
48	5	1.925209693176924	2026-04-22 16:43:13.243213	VALID	2026-04-23 04:43:13.243213
49	5	1.4066991885655968	2026-04-22 15:43:13.243213	VALID	2026-04-23 04:43:13.243213
50	5	0.7956254254217414	2026-04-22 14:43:13.243213	VALID	2026-04-23 04:43:13.243213
51	5	3.373344441496708	2026-04-22 13:43:13.243213	VALID	2026-04-23 04:43:13.243213
52	5	2.6657621505359046	2026-04-22 12:43:13.243213	VALID	2026-04-23 04:43:13.243213
53	5	3.0040390502366465	2026-04-22 11:43:13.243213	VALID	2026-04-23 04:43:13.243213
54	5	1.305663290173058	2026-04-22 10:43:13.243213	VALID	2026-04-23 04:43:13.243213
55	5	2.9291318706226037	2026-04-22 09:43:13.243213	VALID	2026-04-23 04:43:13.243213
56	5	3.3100247812953754	2026-04-22 08:43:13.243213	VALID	2026-04-23 04:43:13.243213
57	5	0.612611559163236	2026-04-22 07:43:13.243213	VALID	2026-04-23 04:43:13.243213
58	5	1.6967687964957106	2026-04-22 06:43:13.243213	VALID	2026-04-23 04:43:13.243213
59	5	3.1620501651337545	2026-04-22 05:43:13.243213	VALID	2026-04-23 04:43:13.243213
60	5	3.9610338389721536	2026-04-22 04:43:13.243213	VALID	2026-04-23 04:43:13.243213
61	6	97.44116457483133	2026-04-23 03:43:13.243213	VALID	2026-04-23 04:43:13.243213
62	6	87.81818688662302	2026-04-23 02:43:13.243213	VALID	2026-04-23 04:43:13.243213
63	6	104.92847119795624	2026-04-23 01:43:13.243213	VALID	2026-04-23 04:43:13.243213
64	6	97.76786769689551	2026-04-23 00:43:13.243213	VALID	2026-04-23 04:43:13.243213
65	6	83.18517856338062	2026-04-22 23:43:13.243213	VALID	2026-04-23 04:43:13.243213
66	6	101.0788110751559	2026-04-22 22:43:13.243213	VALID	2026-04-23 04:43:13.243213
67	6	103.46182079128741	2026-04-22 21:43:13.243213	VALID	2026-04-23 04:43:13.243213
68	6	91.58135569938479	2026-04-22 20:43:13.243213	VALID	2026-04-23 04:43:13.243213
69	6	91.26523533725243	2026-04-22 19:43:13.243213	VALID	2026-04-23 04:43:13.243213
70	6	85.65242820116228	2026-04-22 18:43:13.243213	VALID	2026-04-23 04:43:13.243213
71	6	93.69272440742859	2026-04-22 17:43:13.243213	VALID	2026-04-23 04:43:13.243213
72	6	90.65736236933789	2026-04-22 16:43:13.243213	VALID	2026-04-23 04:43:13.243213
73	6	101.61204273752011	2026-04-22 15:43:13.243213	VALID	2026-04-23 04:43:13.243213
74	6	75.83136449632858	2026-04-22 14:43:13.243213	VALID	2026-04-23 04:43:13.243213
75	6	100.67694815739712	2026-04-22 13:43:13.243213	VALID	2026-04-23 04:43:13.243213
76	6	82.4357849901686	2026-04-22 12:43:13.243213	VALID	2026-04-23 04:43:13.243213
77	6	94.87769659929933	2026-04-22 11:43:13.243213	VALID	2026-04-23 04:43:13.243213
78	6	77.25737602057318	2026-04-22 10:43:13.243213	VALID	2026-04-23 04:43:13.243213
79	6	75.91213749627241	2026-04-22 09:43:13.243213	VALID	2026-04-23 04:43:13.243213
80	6	86.34420560294339	2026-04-22 08:43:13.243213	VALID	2026-04-23 04:43:13.243213
81	6	82.50039642342986	2026-04-22 07:43:13.243213	VALID	2026-04-23 04:43:13.243213
82	6	77.77186710595247	2026-04-22 06:43:13.243213	VALID	2026-04-23 04:43:13.243213
83	6	76.82720922260255	2026-04-22 05:43:13.243213	VALID	2026-04-23 04:43:13.243213
84	6	87.34666796489768	2026-04-22 04:43:13.243213	VALID	2026-04-23 04:43:13.243213
85	7	87.70187925403668	2026-04-23 03:43:13.243213	VALID	2026-04-23 04:43:13.243213
86	7	93.02648439652317	2026-04-23 02:43:13.243213	VALID	2026-04-23 04:43:13.243213
87	7	81.62691162527602	2026-04-23 01:43:13.243213	VALID	2026-04-23 04:43:13.243213
88	7	90.21916463652104	2026-04-23 00:43:13.243213	VALID	2026-04-23 04:43:13.243213
89	7	88.75312054948934	2026-04-22 23:43:13.243213	VALID	2026-04-23 04:43:13.243213
90	7	72.64798404007848	2026-04-22 22:43:13.243213	VALID	2026-04-23 04:43:13.243213
91	7	73.77139599546815	2026-04-22 21:43:13.243213	VALID	2026-04-23 04:43:13.243213
92	7	93.91416552786771	2026-04-22 20:43:13.243213	VALID	2026-04-23 04:43:13.243213
93	7	76.78979966068351	2026-04-22 19:43:13.243213	VALID	2026-04-23 04:43:13.243213
94	7	89.58424898748129	2026-04-22 18:43:13.243213	VALID	2026-04-23 04:43:13.243213
95	7	87.602979589071	2026-04-22 17:43:13.243213	VALID	2026-04-23 04:43:13.243213
96	7	72.59629395327899	2026-04-22 16:43:13.243213	VALID	2026-04-23 04:43:13.243213
97	7	92.92974972606976	2026-04-22 15:43:13.243213	VALID	2026-04-23 04:43:13.243213
98	7	85.58366702522329	2026-04-22 14:43:13.243213	VALID	2026-04-23 04:43:13.243213
99	7	92.76701810976317	2026-04-22 13:43:13.243213	VALID	2026-04-23 04:43:13.243213
100	7	79.87313721076153	2026-04-22 12:43:13.243213	VALID	2026-04-23 04:43:13.243213
101	7	71.22245405308169	2026-04-22 11:43:13.243213	VALID	2026-04-23 04:43:13.243213
102	7	74.47348970547985	2026-04-22 10:43:13.243213	VALID	2026-04-23 04:43:13.243213
103	7	85.02667396766266	2026-04-22 09:43:13.243213	VALID	2026-04-23 04:43:13.243213
104	7	71.56033867664472	2026-04-22 08:43:13.243213	VALID	2026-04-23 04:43:13.243213
105	7	93.54846157812732	2026-04-22 07:43:13.243213	VALID	2026-04-23 04:43:13.243213
106	7	74.48502598201023	2026-04-22 06:43:13.243213	VALID	2026-04-23 04:43:13.243213
107	7	85.86275773070449	2026-04-22 05:43:13.243213	VALID	2026-04-23 04:43:13.243213
108	7	89.35031935282449	2026-04-22 04:43:13.243213	VALID	2026-04-23 04:43:13.243213
109	8	3.316504901937013	2026-04-23 03:43:13.243213	VALID	2026-04-23 04:43:13.243213
110	8	1.1522056172645776	2026-04-23 02:43:13.243213	VALID	2026-04-23 04:43:13.243213
111	8	0.9044913115246084	2026-04-23 01:43:13.243213	VALID	2026-04-23 04:43:13.243213
112	8	3.8050793616167633	2026-04-23 00:43:13.243213	VALID	2026-04-23 04:43:13.243213
113	8	3.471001676370725	2026-04-22 23:43:13.243213	VALID	2026-04-23 04:43:13.243213
114	8	2.1829650127598192	2026-04-22 22:43:13.243213	VALID	2026-04-23 04:43:13.243213
115	8	3.796083350732409	2026-04-22 21:43:13.243213	VALID	2026-04-23 04:43:13.243213
116	8	3.9268222193893845	2026-04-22 20:43:13.243213	VALID	2026-04-23 04:43:13.243213
117	8	0.9884732238887755	2026-04-22 19:43:13.243213	VALID	2026-04-23 04:43:13.243213
118	8	3.198673916173549	2026-04-22 18:43:13.243213	VALID	2026-04-23 04:43:13.243213
119	8	1.5257182590658442	2026-04-22 17:43:13.243213	VALID	2026-04-23 04:43:13.243213
120	8	0.7552647012873608	2026-04-22 16:43:13.243213	VALID	2026-04-23 04:43:13.243213
121	8	1.2919742042000144	2026-04-22 15:43:13.243213	VALID	2026-04-23 04:43:13.243213
122	8	0.8557544157974941	2026-04-22 14:43:13.243213	VALID	2026-04-23 04:43:13.243213
123	8	0.9264389873836899	2026-04-22 13:43:13.243213	VALID	2026-04-23 04:43:13.243213
124	8	2.791877583799844	2026-04-22 12:43:13.243213	VALID	2026-04-23 04:43:13.243213
125	8	1.1909316176271914	2026-04-22 11:43:13.243213	VALID	2026-04-23 04:43:13.243213
126	8	0.7782051924999631	2026-04-22 10:43:13.243213	VALID	2026-04-23 04:43:13.243213
127	8	0.993715490680166	2026-04-22 09:43:13.243213	VALID	2026-04-23 04:43:13.243213
128	8	0.6111717536842707	2026-04-22 08:43:13.243213	VALID	2026-04-23 04:43:13.243213
129	8	3.2281132675175725	2026-04-22 07:43:13.243213	VALID	2026-04-23 04:43:13.243213
130	8	2.2116606997394506	2026-04-22 06:43:13.243213	VALID	2026-04-23 04:43:13.243213
131	8	3.802572258579291	2026-04-22 05:43:13.243213	VALID	2026-04-23 04:43:13.243213
132	8	2.9808702347466935	2026-04-22 04:43:13.243213	VALID	2026-04-23 04:43:13.243213
133	9	77.8071108352744	2026-04-23 03:43:13.243213	VALID	2026-04-23 04:43:13.243213
134	9	74.7650625178332	2026-04-23 02:43:13.243213	VALID	2026-04-23 04:43:13.243213
135	9	79.30347945288142	2026-04-23 01:43:13.243213	VALID	2026-04-23 04:43:13.243213
136	9	79.73700621641035	2026-04-23 00:43:13.243213	VALID	2026-04-23 04:43:13.243213
137	9	87.80056297212283	2026-04-22 23:43:13.243213	VALID	2026-04-23 04:43:13.243213
138	9	78.93483492362985	2026-04-22 22:43:13.243213	VALID	2026-04-23 04:43:13.243213
139	9	82.03935006946244	2026-04-22 21:43:13.243213	VALID	2026-04-23 04:43:13.243213
140	9	82.67343261902187	2026-04-22 20:43:13.243213	VALID	2026-04-23 04:43:13.243213
141	9	89.62982136135017	2026-04-22 19:43:13.243213	VALID	2026-04-23 04:43:13.243213
142	9	89.54027127844135	2026-04-22 18:43:13.243213	VALID	2026-04-23 04:43:13.243213
143	9	75.09845271717464	2026-04-22 17:43:13.243213	VALID	2026-04-23 04:43:13.243213
144	9	75.88719946546532	2026-04-22 16:43:13.243213	VALID	2026-04-23 04:43:13.243213
145	9	76.39923261742473	2026-04-22 15:43:13.243213	VALID	2026-04-23 04:43:13.243213
146	9	87.19924338219502	2026-04-22 14:43:13.243213	VALID	2026-04-23 04:43:13.243213
147	9	88.39200663861362	2026-04-22 13:43:13.243213	VALID	2026-04-23 04:43:13.243213
148	9	75.56063488795293	2026-04-22 12:43:13.243213	VALID	2026-04-23 04:43:13.243213
149	9	82.79279628177677	2026-04-22 11:43:13.243213	VALID	2026-04-23 04:43:13.243213
150	9	77.75982722875922	2026-04-22 10:43:13.243213	VALID	2026-04-23 04:43:13.243213
151	9	94.63442063605484	2026-04-22 09:43:13.243213	VALID	2026-04-23 04:43:13.243213
152	9	94.6096878187773	2026-04-22 08:43:13.243213	VALID	2026-04-23 04:43:13.243213
153	9	76.35357720878163	2026-04-22 07:43:13.243213	VALID	2026-04-23 04:43:13.243213
154	9	93.05741527542966	2026-04-22 06:43:13.243213	VALID	2026-04-23 04:43:13.243213
155	9	93.30428094361542	2026-04-22 05:43:13.243213	VALID	2026-04-23 04:43:13.243213
156	9	77.12580515513706	2026-04-22 04:43:13.243213	VALID	2026-04-23 04:43:13.243213
157	10	98.73834019183357	2026-04-23 03:43:13.243213	VALID	2026-04-23 04:43:13.243213
158	10	86.44701998522086	2026-04-23 02:43:13.243213	VALID	2026-04-23 04:43:13.243213
159	10	88.88887107685225	2026-04-23 01:43:13.243213	VALID	2026-04-23 04:43:13.243213
160	10	97.33816613463138	2026-04-23 00:43:13.243213	VALID	2026-04-23 04:43:13.243213
161	10	93.82713847924276	2026-04-22 23:43:13.243213	VALID	2026-04-23 04:43:13.243213
162	10	88.20904898203399	2026-04-22 22:43:13.243213	VALID	2026-04-23 04:43:13.243213
163	10	95.49791266288541	2026-04-22 21:43:13.243213	VALID	2026-04-23 04:43:13.243213
164	10	99.51678686243633	2026-04-22 20:43:13.243213	VALID	2026-04-23 04:43:13.243213
165	10	79.1966652248198	2026-04-22 19:43:13.243213	VALID	2026-04-23 04:43:13.243213
166	10	86.35264696269911	2026-04-22 18:43:13.243213	VALID	2026-04-23 04:43:13.243213
167	10	88.383665432937	2026-04-22 17:43:13.243213	VALID	2026-04-23 04:43:13.243213
168	10	100.63324196998266	2026-04-22 16:43:13.243213	VALID	2026-04-23 04:43:13.243213
169	10	101.39429688907451	2026-04-22 15:43:13.243213	VALID	2026-04-23 04:43:13.243213
170	10	84.3389238743992	2026-04-22 14:43:13.243213	VALID	2026-04-23 04:43:13.243213
171	10	93.35601095560037	2026-04-22 13:43:13.243213	VALID	2026-04-23 04:43:13.243213
172	10	98.72746853079872	2026-04-22 12:43:13.243213	VALID	2026-04-23 04:43:13.243213
173	10	95.06534835151236	2026-04-22 11:43:13.243213	VALID	2026-04-23 04:43:13.243213
174	10	82.79321214905913	2026-04-22 10:43:13.243213	VALID	2026-04-23 04:43:13.243213
175	10	76.11659757899773	2026-04-22 09:43:13.243213	VALID	2026-04-23 04:43:13.243213
176	10	92.1341366736892	2026-04-22 08:43:13.243213	VALID	2026-04-23 04:43:13.243213
177	10	75.06123396751119	2026-04-22 07:43:13.243213	VALID	2026-04-23 04:43:13.243213
178	10	98.1868089191332	2026-04-22 06:43:13.243213	VALID	2026-04-23 04:43:13.243213
179	10	91.62468754146883	2026-04-22 05:43:13.243213	VALID	2026-04-23 04:43:13.243213
180	10	85.3088867939878	2026-04-22 04:43:13.243213	VALID	2026-04-23 04:43:13.243213
181	11	92.60338723047866	2026-04-23 03:43:13.243213	VALID	2026-04-23 04:43:13.243213
182	11	78.14515399344756	2026-04-23 02:43:13.243213	VALID	2026-04-23 04:43:13.243213
183	11	77.88267170948646	2026-04-23 01:43:13.243213	VALID	2026-04-23 04:43:13.243213
184	11	83.78858646816664	2026-04-23 00:43:13.243213	VALID	2026-04-23 04:43:13.243213
185	11	79.82060605754609	2026-04-22 23:43:13.243213	VALID	2026-04-23 04:43:13.243213
186	11	91.76650518935857	2026-04-22 22:43:13.243213	VALID	2026-04-23 04:43:13.243213
187	11	90.83381633667241	2026-04-22 21:43:13.243213	VALID	2026-04-23 04:43:13.243213
188	11	85.95944851634542	2026-04-22 20:43:13.243213	VALID	2026-04-23 04:43:13.243213
189	11	73.16443049274784	2026-04-22 19:43:13.243213	VALID	2026-04-23 04:43:13.243213
190	11	78.7425288442019	2026-04-22 18:43:13.243213	VALID	2026-04-23 04:43:13.243213
191	11	79.41941115044673	2026-04-22 17:43:13.243213	VALID	2026-04-23 04:43:13.243213
192	11	87.30739599943213	2026-04-22 16:43:13.243213	VALID	2026-04-23 04:43:13.243213
193	11	79.05471797321856	2026-04-22 15:43:13.243213	VALID	2026-04-23 04:43:13.243213
194	11	89.72786423235112	2026-04-22 14:43:13.243213	VALID	2026-04-23 04:43:13.243213
195	11	79.21002188843481	2026-04-22 13:43:13.243213	VALID	2026-04-23 04:43:13.243213
196	11	84.10372644113869	2026-04-22 12:43:13.243213	VALID	2026-04-23 04:43:13.243213
197	11	91.07527394015021	2026-04-22 11:43:13.243213	VALID	2026-04-23 04:43:13.243213
198	11	83.05195571845455	2026-04-22 10:43:13.243213	VALID	2026-04-23 04:43:13.243213
199	11	90.32967787992467	2026-04-22 09:43:13.243213	VALID	2026-04-23 04:43:13.243213
200	11	81.3261270721434	2026-04-22 08:43:13.243213	VALID	2026-04-23 04:43:13.243213
201	11	86.00686980342232	2026-04-22 07:43:13.243213	VALID	2026-04-23 04:43:13.243213
202	11	78.5832613659556	2026-04-22 06:43:13.243213	VALID	2026-04-23 04:43:13.243213
203	11	90.99939174728647	2026-04-22 05:43:13.243213	VALID	2026-04-23 04:43:13.243213
204	11	83.4939860741035	2026-04-22 04:43:13.243213	VALID	2026-04-23 04:43:13.243213
205	12	2.6386422312802553	2026-04-23 03:43:13.243213	VALID	2026-04-23 04:43:13.243213
206	12	2.244879295945826	2026-04-23 02:43:13.243213	VALID	2026-04-23 04:43:13.243213
207	12	3.0344516922359634	2026-04-23 01:43:13.243213	VALID	2026-04-23 04:43:13.243213
208	12	1.9901597938188909	2026-04-23 00:43:13.243213	VALID	2026-04-23 04:43:13.243213
209	12	1.3154764590031873	2026-04-22 23:43:13.243213	VALID	2026-04-23 04:43:13.243213
210	12	0.660603670667379	2026-04-22 22:43:13.243213	VALID	2026-04-23 04:43:13.243213
211	12	3.5814706170996646	2026-04-22 21:43:13.243213	VALID	2026-04-23 04:43:13.243213
212	12	2.8946196040773846	2026-04-22 20:43:13.243213	VALID	2026-04-23 04:43:13.243213
213	12	3.854533137662767	2026-04-22 19:43:13.243213	VALID	2026-04-23 04:43:13.243213
214	12	2.0652509649695765	2026-04-22 18:43:13.243213	VALID	2026-04-23 04:43:13.243213
215	12	3.098032913617425	2026-04-22 17:43:13.243213	VALID	2026-04-23 04:43:13.243213
216	12	3.7963537967483894	2026-04-22 16:43:13.243213	VALID	2026-04-23 04:43:13.243213
217	12	3.7937241641594004	2026-04-22 15:43:13.243213	VALID	2026-04-23 04:43:13.243213
218	12	1.2998054840701787	2026-04-22 14:43:13.243213	VALID	2026-04-23 04:43:13.243213
219	12	2.453962277697955	2026-04-22 13:43:13.243213	VALID	2026-04-23 04:43:13.243213
220	12	1.2217550032515376	2026-04-22 12:43:13.243213	VALID	2026-04-23 04:43:13.243213
221	12	0.5104871782797052	2026-04-22 11:43:13.243213	VALID	2026-04-23 04:43:13.243213
222	12	1.7575257488446259	2026-04-22 10:43:13.243213	VALID	2026-04-23 04:43:13.243213
223	12	0.7159998878606183	2026-04-22 09:43:13.243213	VALID	2026-04-23 04:43:13.243213
224	12	0.6775791176205239	2026-04-22 08:43:13.243213	VALID	2026-04-23 04:43:13.243213
225	12	3.1220296414111863	2026-04-22 07:43:13.243213	VALID	2026-04-23 04:43:13.243213
226	12	0.58441502001757	2026-04-22 06:43:13.243213	VALID	2026-04-23 04:43:13.243213
227	12	2.4200298182560944	2026-04-22 05:43:13.243213	VALID	2026-04-23 04:43:13.243213
228	12	3.306206853614019	2026-04-22 04:43:13.243213	VALID	2026-04-23 04:43:13.243213
229	13	74.16139762284323	2026-04-23 03:43:13.243213	VALID	2026-04-23 04:43:13.243213
230	13	94.98094060217437	2026-04-23 02:43:13.243213	VALID	2026-04-23 04:43:13.243213
231	13	74.27576216598989	2026-04-23 01:43:13.243213	VALID	2026-04-23 04:43:13.243213
232	13	92.34588949641265	2026-04-23 00:43:13.243213	VALID	2026-04-23 04:43:13.243213
233	13	86.16125177009789	2026-04-22 23:43:13.243213	VALID	2026-04-23 04:43:13.243213
234	13	74.19492453101941	2026-04-22 22:43:13.243213	VALID	2026-04-23 04:43:13.243213
235	13	84.68643431065962	2026-04-22 21:43:13.243213	VALID	2026-04-23 04:43:13.243213
236	13	81.63562586768437	2026-04-22 20:43:13.243213	VALID	2026-04-23 04:43:13.243213
237	13	71.94276024616738	2026-04-22 19:43:13.243213	VALID	2026-04-23 04:43:13.243213
238	13	76.56502310885327	2026-04-22 18:43:13.243213	VALID	2026-04-23 04:43:13.243213
239	13	80.12987479483573	2026-04-22 17:43:13.243213	VALID	2026-04-23 04:43:13.243213
240	13	90.6257343345544	2026-04-22 16:43:13.243213	VALID	2026-04-23 04:43:13.243213
241	13	78.71507708012437	2026-04-22 15:43:13.243213	VALID	2026-04-23 04:43:13.243213
242	13	86.03778512238085	2026-04-22 14:43:13.243213	VALID	2026-04-23 04:43:13.243213
243	13	78.06370723727846	2026-04-22 13:43:13.243213	VALID	2026-04-23 04:43:13.243213
244	13	86.2364466004859	2026-04-22 12:43:13.243213	VALID	2026-04-23 04:43:13.243213
245	13	81.24184588723756	2026-04-22 11:43:13.243213	VALID	2026-04-23 04:43:13.243213
246	13	77.44126981574595	2026-04-22 10:43:13.243213	VALID	2026-04-23 04:43:13.243213
247	13	88.21909451839967	2026-04-22 09:43:13.243213	VALID	2026-04-23 04:43:13.243213
248	13	82.04584311023918	2026-04-22 08:43:13.243213	VALID	2026-04-23 04:43:13.243213
249	13	79.63378488135389	2026-04-22 07:43:13.243213	VALID	2026-04-23 04:43:13.243213
250	13	71.82666297248849	2026-04-22 06:43:13.243213	VALID	2026-04-23 04:43:13.243213
251	13	75.46613766035955	2026-04-22 05:43:13.243213	VALID	2026-04-23 04:43:13.243213
252	13	81.5586294988515	2026-04-22 04:43:13.243213	VALID	2026-04-23 04:43:13.243213
253	14	2.0136434902849545	2026-04-23 03:43:13.243213	VALID	2026-04-23 04:43:13.243213
254	14	3.575495657666364	2026-04-23 02:43:13.243213	VALID	2026-04-23 04:43:13.243213
255	14	1.0139890130543048	2026-04-23 01:43:13.243213	VALID	2026-04-23 04:43:13.243213
256	14	1.117256776078933	2026-04-23 00:43:13.243213	VALID	2026-04-23 04:43:13.243213
257	14	2.4494664835640374	2026-04-22 23:43:13.243213	VALID	2026-04-23 04:43:13.243213
258	14	0.6958243418447947	2026-04-22 22:43:13.243213	VALID	2026-04-23 04:43:13.243213
259	14	1.0463119813234374	2026-04-22 21:43:13.243213	VALID	2026-04-23 04:43:13.243213
260	14	3.7884354959983755	2026-04-22 20:43:13.243213	VALID	2026-04-23 04:43:13.243213
261	14	2.942182615115929	2026-04-22 19:43:13.243213	VALID	2026-04-23 04:43:13.243213
262	14	2.400298527994228	2026-04-22 18:43:13.243213	VALID	2026-04-23 04:43:13.243213
263	14	3.0202873899735305	2026-04-22 17:43:13.243213	VALID	2026-04-23 04:43:13.243213
264	14	2.4284567413258573	2026-04-22 16:43:13.243213	VALID	2026-04-23 04:43:13.243213
265	14	1.8765099044722109	2026-04-22 15:43:13.243213	VALID	2026-04-23 04:43:13.243213
266	14	2.3913068204373222	2026-04-22 14:43:13.243213	VALID	2026-04-23 04:43:13.243213
267	14	0.523280074722028	2026-04-22 13:43:13.243213	VALID	2026-04-23 04:43:13.243213
268	14	0.983464647106338	2026-04-22 12:43:13.243213	VALID	2026-04-23 04:43:13.243213
269	14	1.3113127298000136	2026-04-22 11:43:13.243213	VALID	2026-04-23 04:43:13.243213
270	14	2.2020194102812045	2026-04-22 10:43:13.243213	VALID	2026-04-23 04:43:13.243213
271	14	1.9521607201557925	2026-04-22 09:43:13.243213	VALID	2026-04-23 04:43:13.243213
272	14	0.855322120202477	2026-04-22 08:43:13.243213	VALID	2026-04-23 04:43:13.243213
273	14	1.8017173465107192	2026-04-22 07:43:13.243213	VALID	2026-04-23 04:43:13.243213
274	14	3.1377034556813754	2026-04-22 06:43:13.243213	VALID	2026-04-23 04:43:13.243213
275	14	1.2483572780848635	2026-04-22 05:43:13.243213	VALID	2026-04-23 04:43:13.243213
276	14	2.736115016653413	2026-04-22 04:43:13.243213	VALID	2026-04-23 04:43:13.243213
\.


--
-- Data for Name: sensor_type; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.sensor_type (id, organization_id, name, unit, default_warning_threshold, default_critical_threshold, created_at) FROM stdin;
1	1	Temperature	°C	\N	\N	2026-04-21 02:33:21.608088
2	1	Vibration	mm/s	\N	\N	2026-04-21 02:33:21.608088
3	1	Pressure	bar	\N	\N	2026-04-21 02:33:21.608088
4	4	Temperature	°C	\N	\N	2026-04-30 00:41:05.747728
5	4	Pressure	bar	\N	\N	2026-04-30 00:41:05.747728
6	4	Rotational Speed	RPM	\N	\N	2026-04-30 00:41:05.747728
7	4	Thermal Efficiency	%	\N	\N	2026-04-30 00:41:05.747728
8	4	Airflow Dynamics	m³/s	\N	\N	2026-04-30 00:41:05.747728
9	4	Pressure Stability	bar	\N	\N	2026-04-30 00:41:05.747728
10	4	Vibration	mm/s	\N	\N	2026-04-30 00:41:05.747728
11	4	Temperature Stage	°C	\N	\N	2026-04-30 00:41:05.747728
12	4	Efficiency Parameter	%	\N	\N	2026-04-30 00:41:05.747728
13	4	Flow Variation	L/min	\N	\N	2026-04-30 00:41:05.747728
14	4	Vibration Amplitude	mm/s	\N	\N	2026-04-30 00:41:05.747728
15	4	Pressure Ratio	bar	\N	\N	2026-04-30 00:41:05.747728
16	4	Thermal Load	°C	\N	\N	2026-04-30 00:41:05.747728
17	4	Mechanical Stress	MPa	\N	\N	2026-04-30 00:41:05.747728
18	4	Turbine Behavior	RPM	\N	\N	2026-04-30 00:41:05.747728
19	4	Air Intake Signal	m³/s	\N	\N	2026-04-30 00:41:05.747728
20	4	Pressure Fluctuation	bar	\N	\N	2026-04-30 00:41:05.747728
21	4	Heat Dissipation	°C	\N	\N	2026-04-30 00:41:05.747728
22	4	Mechanical Oscillation	mm/s	\N	\N	2026-04-30 00:41:05.747728
23	4	System Efficiency	%	\N	\N	2026-04-30 00:41:05.747728
24	4	Dynamic Vibration	mm/s	\N	\N	2026-04-30 00:41:05.747728
\.


--
-- Data for Name: test_machine; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.test_machine (id, name) FROM stdin;
\.


--
-- Data for Name: test_table; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.test_table (id, name) FROM stdin;
1	MiniMaxi
\.


--
-- Data for Name: threshold; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.threshold (id, organization_id, asset_type_id, sensor_type_id, warning_value, critical_value, updated_by_user_id, updated_at) FROM stdin;
1	1	1	1	80.00	90.00	1	2026-04-23 04:28:45.410017
2	1	1	2	2.00	4.00	1	2026-04-23 04:28:45.410017
3	1	1	3	80.00	100.00	1	2026-04-23 04:28:45.410017
4	1	2	1	75.00	85.00	1	2026-04-23 04:28:45.410017
5	1	2	2	1.50	3.00	1	2026-04-23 04:28:45.410017
6	1	3	1	70.00	80.00	1	2026-04-23 04:28:45.410017
7	1	3	3	70.00	90.00	1	2026-04-23 04:28:45.410017
\.


--
-- Data for Name: user_asset_assignment; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.user_asset_assignment (id, organization_id, user_id, machine_id, created_at) FROM stdin;
\.


--
-- Data for Name: user_device_token; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.user_device_token (id, user_id, device_platform, device_token, last_seen_at) FROM stdin;
\.


--
-- Data for Name: work_order; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.work_order (id, organization_id, machine_id, issue_id, created_by_user_id, assigned_to_user_id, priority, status, due_date, title, description, ai_suggested, created_at, closed_at) FROM stdin;
1	1	1	\N	1	2	HIGH	OPEN	2026-04-24	Inspect CNC spindle	Check spindle vibration and temperature anomalies.	f	2026-04-21 03:01:17.75128	\N
2	1	1	\N	1	2	HIGH	OPEN	2026-04-25	Replace worn bearing	Bearing shows abnormal vibration trend.	t	2026-04-21 01:15:24.072056	\N
3	1	1	\N	1	2	HIGH	OPEN	2026-04-30	Inspect CNC spindle	Check spindle vibration	f	2026-04-21 22:53:55.252525	\N
4	4	7	\N	3	3	HIGH	IN_PROGRESS	2026-04-26	Inspect CNC spindle bearing	Abnormal vibration detected. Inspect and replace bearing if needed.	t	2026-04-23 04:42:26.550522	\N
5	4	9	\N	3	3	CRITICAL	OPEN	2026-04-24	Emergency maintenance - Compressor	Critical pressure spike detected. Immediate inspection required.	t	2026-04-23 04:42:26.550522	\N
6	4	11	\N	3	3	MEDIUM	OPEN	2026-04-28	Preventive maintenance - Turbine	Temperature trending upward. Preventive maintenance advised.	t	2026-04-23 04:42:26.550522	\N
7	4	8	\N	3	3	LOW	COMPLETED	2026-04-20	Routine check - Hydraulic Pump	Monthly routine inspection completed successfully.	f	2026-04-18 04:42:26.550522	\N
8	4	10	\N	3	3	LOW	COMPLETED	2026-04-18	Motor Delta monthly inspection	All readings normal. No issues found.	f	2026-04-15 04:42:26.550522	\N
\.


--
-- Data for Name: work_order_completion; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.work_order_completion (id, work_order_id, completed_by_user_id, action_taken, root_cause, time_spent_minutes, additional_notes, completed_at) FROM stdin;
\.


--
-- Data for Name: work_order_spare_part; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.work_order_spare_part (id, completion_id, part_name, quantity) FROM stdin;
\.


--
-- Name: ai_model_info_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.ai_model_info_id_seq', 1, false);


--
-- Name: app_user_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.app_user_id_seq', 27, true);


--
-- Name: asset_type_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.asset_type_id_seq', 3, true);


--
-- Name: chat_conversation_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.chat_conversation_id_seq', 1, false);


--
-- Name: chat_message_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.chat_message_id_seq', 1, false);


--
-- Name: issue_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.issue_id_seq', 1, false);


--
-- Name: machine_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.machine_id_seq', 11, true);


--
-- Name: notification_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.notification_id_seq', 7, true);


--
-- Name: organization_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.organization_id_seq', 16, true);


--
-- Name: organization_request_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.organization_request_id_seq', 13, true);


--
-- Name: prediction_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.prediction_id_seq', 401, true);


--
-- Name: sensor_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.sensor_id_seq', 119, true);


--
-- Name: sensor_reading_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.sensor_reading_id_seq', 276, true);


--
-- Name: sensor_type_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.sensor_type_id_seq', 24, true);


--
-- Name: test_machine_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.test_machine_id_seq', 1, false);


--
-- Name: test_table_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.test_table_id_seq', 1, true);


--
-- Name: threshold_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.threshold_id_seq', 7, true);


--
-- Name: user_asset_assignment_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.user_asset_assignment_id_seq', 1, false);


--
-- Name: user_device_token_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.user_device_token_id_seq', 1, false);


--
-- Name: work_order_completion_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.work_order_completion_id_seq', 1, false);


--
-- Name: work_order_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.work_order_id_seq', 8, true);


--
-- Name: work_order_spare_part_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.work_order_spare_part_id_seq', 1, false);


--
-- Name: ai_model_info ai_model_info_organization_id_model_name_version_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.ai_model_info
    ADD CONSTRAINT ai_model_info_organization_id_model_name_version_key UNIQUE (organization_id, model_name, version);


--
-- Name: ai_model_info ai_model_info_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.ai_model_info
    ADD CONSTRAINT ai_model_info_pkey PRIMARY KEY (id);


--
-- Name: app_user app_user_email_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.app_user
    ADD CONSTRAINT app_user_email_key UNIQUE (email);


--
-- Name: app_user app_user_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.app_user
    ADD CONSTRAINT app_user_pkey PRIMARY KEY (id);


--
-- Name: asset_type asset_type_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.asset_type
    ADD CONSTRAINT asset_type_pkey PRIMARY KEY (id);


--
-- Name: chat_conversation chat_conversation_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.chat_conversation
    ADD CONSTRAINT chat_conversation_pkey PRIMARY KEY (id);


--
-- Name: chat_message chat_message_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.chat_message
    ADD CONSTRAINT chat_message_pkey PRIMARY KEY (id);


--
-- Name: issue issue_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.issue
    ADD CONSTRAINT issue_pkey PRIMARY KEY (id);


--
-- Name: machine machine_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.machine
    ADD CONSTRAINT machine_pkey PRIMARY KEY (id);


--
-- Name: notification notification_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.notification
    ADD CONSTRAINT notification_pkey PRIMARY KEY (id);


--
-- Name: organization organization_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.organization
    ADD CONSTRAINT organization_pkey PRIMARY KEY (id);


--
-- Name: organization_request organization_request_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.organization_request
    ADD CONSTRAINT organization_request_pkey PRIMARY KEY (id);


--
-- Name: prediction prediction_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.prediction
    ADD CONSTRAINT prediction_pkey PRIMARY KEY (id);


--
-- Name: sensor sensor_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.sensor
    ADD CONSTRAINT sensor_pkey PRIMARY KEY (id);


--
-- Name: sensor_reading sensor_reading_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.sensor_reading
    ADD CONSTRAINT sensor_reading_pkey PRIMARY KEY (id);


--
-- Name: sensor_type sensor_type_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.sensor_type
    ADD CONSTRAINT sensor_type_pkey PRIMARY KEY (id);


--
-- Name: test_machine test_machine_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.test_machine
    ADD CONSTRAINT test_machine_pkey PRIMARY KEY (id);


--
-- Name: test_table test_table_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.test_table
    ADD CONSTRAINT test_table_pkey PRIMARY KEY (id);


--
-- Name: threshold threshold_organization_id_asset_type_id_sensor_type_id_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.threshold
    ADD CONSTRAINT threshold_organization_id_asset_type_id_sensor_type_id_key UNIQUE (organization_id, asset_type_id, sensor_type_id);


--
-- Name: threshold threshold_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.threshold
    ADD CONSTRAINT threshold_pkey PRIMARY KEY (id);


--
-- Name: machine uq_machine_asset_id; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.machine
    ADD CONSTRAINT uq_machine_asset_id UNIQUE (asset_id);


--
-- Name: user_asset_assignment user_asset_assignment_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.user_asset_assignment
    ADD CONSTRAINT user_asset_assignment_pkey PRIMARY KEY (id);


--
-- Name: user_asset_assignment user_asset_assignment_user_id_machine_id_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.user_asset_assignment
    ADD CONSTRAINT user_asset_assignment_user_id_machine_id_key UNIQUE (user_id, machine_id);


--
-- Name: user_device_token user_device_token_device_token_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.user_device_token
    ADD CONSTRAINT user_device_token_device_token_key UNIQUE (device_token);


--
-- Name: user_device_token user_device_token_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.user_device_token
    ADD CONSTRAINT user_device_token_pkey PRIMARY KEY (id);


--
-- Name: work_order_completion work_order_completion_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.work_order_completion
    ADD CONSTRAINT work_order_completion_pkey PRIMARY KEY (id);


--
-- Name: work_order_completion work_order_completion_work_order_id_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.work_order_completion
    ADD CONSTRAINT work_order_completion_work_order_id_key UNIQUE (work_order_id);


--
-- Name: work_order work_order_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.work_order
    ADD CONSTRAINT work_order_pkey PRIMARY KEY (id);


--
-- Name: work_order_spare_part work_order_spare_part_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.work_order_spare_part
    ADD CONSTRAINT work_order_spare_part_pkey PRIMARY KEY (id);


--
-- Name: idx_chat_user_time; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_chat_user_time ON public.chat_conversation USING btree (user_id, last_message_at DESC);


--
-- Name: idx_machine_org; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_machine_org ON public.machine USING btree (organization_id);


--
-- Name: idx_notif_user_read_time; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_notif_user_read_time ON public.notification USING btree (recipient_user_id, is_read, created_at DESC);


--
-- Name: idx_reading_sensor_time; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_reading_sensor_time ON public.sensor_reading USING btree (sensor_id, reading_time DESC);


--
-- Name: idx_sensor_machine; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_sensor_machine ON public.sensor USING btree (machine_id);


--
-- Name: idx_wo_assigned_status; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_wo_assigned_status ON public.work_order USING btree (assigned_to_user_id, status);


--
-- Name: ai_model_info ai_model_info_organization_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.ai_model_info
    ADD CONSTRAINT ai_model_info_organization_id_fkey FOREIGN KEY (organization_id) REFERENCES public.organization(id);


--
-- Name: app_user app_user_organization_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.app_user
    ADD CONSTRAINT app_user_organization_id_fkey FOREIGN KEY (organization_id) REFERENCES public.organization(id);


--
-- Name: asset_type asset_type_organization_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.asset_type
    ADD CONSTRAINT asset_type_organization_id_fkey FOREIGN KEY (organization_id) REFERENCES public.organization(id);


--
-- Name: chat_conversation chat_conversation_context_machine_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.chat_conversation
    ADD CONSTRAINT chat_conversation_context_machine_id_fkey FOREIGN KEY (context_machine_id) REFERENCES public.machine(id);


--
-- Name: chat_conversation chat_conversation_context_work_order_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.chat_conversation
    ADD CONSTRAINT chat_conversation_context_work_order_id_fkey FOREIGN KEY (context_work_order_id) REFERENCES public.work_order(id);


--
-- Name: chat_conversation chat_conversation_organization_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.chat_conversation
    ADD CONSTRAINT chat_conversation_organization_id_fkey FOREIGN KEY (organization_id) REFERENCES public.organization(id);


--
-- Name: chat_conversation chat_conversation_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.chat_conversation
    ADD CONSTRAINT chat_conversation_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.app_user(id);


--
-- Name: chat_message chat_message_conversation_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.chat_message
    ADD CONSTRAINT chat_message_conversation_id_fkey FOREIGN KEY (conversation_id) REFERENCES public.chat_conversation(id);


--
-- Name: issue issue_created_by_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.issue
    ADD CONSTRAINT issue_created_by_user_id_fkey FOREIGN KEY (created_by_user_id) REFERENCES public.app_user(id);


--
-- Name: issue issue_machine_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.issue
    ADD CONSTRAINT issue_machine_id_fkey FOREIGN KEY (machine_id) REFERENCES public.machine(id);


--
-- Name: issue issue_organization_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.issue
    ADD CONSTRAINT issue_organization_id_fkey FOREIGN KEY (organization_id) REFERENCES public.organization(id);


--
-- Name: issue issue_prediction_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.issue
    ADD CONSTRAINT issue_prediction_id_fkey FOREIGN KEY (prediction_id) REFERENCES public.prediction(id);


--
-- Name: machine machine_asset_type_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.machine
    ADD CONSTRAINT machine_asset_type_id_fkey FOREIGN KEY (asset_type_id) REFERENCES public.asset_type(id);


--
-- Name: machine machine_organization_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.machine
    ADD CONSTRAINT machine_organization_id_fkey FOREIGN KEY (organization_id) REFERENCES public.organization(id);


--
-- Name: notification notification_machine_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.notification
    ADD CONSTRAINT notification_machine_id_fkey FOREIGN KEY (machine_id) REFERENCES public.machine(id);


--
-- Name: notification notification_organization_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.notification
    ADD CONSTRAINT notification_organization_id_fkey FOREIGN KEY (organization_id) REFERENCES public.organization(id);


--
-- Name: notification notification_prediction_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.notification
    ADD CONSTRAINT notification_prediction_id_fkey FOREIGN KEY (prediction_id) REFERENCES public.prediction(id);


--
-- Name: notification notification_recipient_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.notification
    ADD CONSTRAINT notification_recipient_user_id_fkey FOREIGN KEY (recipient_user_id) REFERENCES public.app_user(id);


--
-- Name: notification notification_work_order_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.notification
    ADD CONSTRAINT notification_work_order_id_fkey FOREIGN KEY (work_order_id) REFERENCES public.work_order(id);


--
-- Name: prediction prediction_machine_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.prediction
    ADD CONSTRAINT prediction_machine_id_fkey FOREIGN KEY (machine_id) REFERENCES public.machine(id);


--
-- Name: prediction prediction_organization_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.prediction
    ADD CONSTRAINT prediction_organization_id_fkey FOREIGN KEY (organization_id) REFERENCES public.organization(id);


--
-- Name: sensor sensor_machine_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.sensor
    ADD CONSTRAINT sensor_machine_id_fkey FOREIGN KEY (machine_id) REFERENCES public.machine(id);


--
-- Name: sensor sensor_organization_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.sensor
    ADD CONSTRAINT sensor_organization_id_fkey FOREIGN KEY (organization_id) REFERENCES public.organization(id);


--
-- Name: sensor_reading sensor_reading_sensor_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.sensor_reading
    ADD CONSTRAINT sensor_reading_sensor_id_fkey FOREIGN KEY (sensor_id) REFERENCES public.sensor(id);


--
-- Name: sensor sensor_sensor_type_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.sensor
    ADD CONSTRAINT sensor_sensor_type_id_fkey FOREIGN KEY (sensor_type_id) REFERENCES public.sensor_type(id);


--
-- Name: sensor_type sensor_type_organization_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.sensor_type
    ADD CONSTRAINT sensor_type_organization_id_fkey FOREIGN KEY (organization_id) REFERENCES public.organization(id);


--
-- Name: threshold threshold_asset_type_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.threshold
    ADD CONSTRAINT threshold_asset_type_id_fkey FOREIGN KEY (asset_type_id) REFERENCES public.asset_type(id);


--
-- Name: threshold threshold_organization_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.threshold
    ADD CONSTRAINT threshold_organization_id_fkey FOREIGN KEY (organization_id) REFERENCES public.organization(id);


--
-- Name: threshold threshold_sensor_type_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.threshold
    ADD CONSTRAINT threshold_sensor_type_id_fkey FOREIGN KEY (sensor_type_id) REFERENCES public.sensor_type(id);


--
-- Name: threshold threshold_updated_by_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.threshold
    ADD CONSTRAINT threshold_updated_by_user_id_fkey FOREIGN KEY (updated_by_user_id) REFERENCES public.app_user(id);


--
-- Name: user_asset_assignment user_asset_assignment_machine_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.user_asset_assignment
    ADD CONSTRAINT user_asset_assignment_machine_id_fkey FOREIGN KEY (machine_id) REFERENCES public.machine(id);


--
-- Name: user_asset_assignment user_asset_assignment_organization_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.user_asset_assignment
    ADD CONSTRAINT user_asset_assignment_organization_id_fkey FOREIGN KEY (organization_id) REFERENCES public.organization(id);


--
-- Name: user_asset_assignment user_asset_assignment_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.user_asset_assignment
    ADD CONSTRAINT user_asset_assignment_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.app_user(id);


--
-- Name: user_device_token user_device_token_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.user_device_token
    ADD CONSTRAINT user_device_token_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.app_user(id);


--
-- Name: work_order work_order_assigned_to_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.work_order
    ADD CONSTRAINT work_order_assigned_to_user_id_fkey FOREIGN KEY (assigned_to_user_id) REFERENCES public.app_user(id);


--
-- Name: work_order_completion work_order_completion_completed_by_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.work_order_completion
    ADD CONSTRAINT work_order_completion_completed_by_user_id_fkey FOREIGN KEY (completed_by_user_id) REFERENCES public.app_user(id);


--
-- Name: work_order_completion work_order_completion_work_order_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.work_order_completion
    ADD CONSTRAINT work_order_completion_work_order_id_fkey FOREIGN KEY (work_order_id) REFERENCES public.work_order(id);


--
-- Name: work_order work_order_created_by_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.work_order
    ADD CONSTRAINT work_order_created_by_user_id_fkey FOREIGN KEY (created_by_user_id) REFERENCES public.app_user(id);


--
-- Name: work_order work_order_issue_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.work_order
    ADD CONSTRAINT work_order_issue_id_fkey FOREIGN KEY (issue_id) REFERENCES public.issue(id);


--
-- Name: work_order work_order_machine_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.work_order
    ADD CONSTRAINT work_order_machine_id_fkey FOREIGN KEY (machine_id) REFERENCES public.machine(id);


--
-- Name: work_order work_order_organization_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.work_order
    ADD CONSTRAINT work_order_organization_id_fkey FOREIGN KEY (organization_id) REFERENCES public.organization(id);


--
-- Name: work_order_spare_part work_order_spare_part_completion_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.work_order_spare_part
    ADD CONSTRAINT work_order_spare_part_completion_id_fkey FOREIGN KEY (completion_id) REFERENCES public.work_order_completion(id);


--
-- PostgreSQL database dump complete
--

\unrestrict xGitutt6N0Ozmb546wxZFs9e9BOhLSlpS9UOWFrVde2GTjjuKf4yvGMKCMiiLjc

