--
-- PostgreSQL database dump
--

-- Started on 2007-03-25 01:04:29 WIT

SET client_encoding = 'UTF8';
SET check_function_bodies = false;
SET client_min_messages = warning;

--
-- TOC entry 1757 (class 0 OID 0)
-- Dependencies: 4
-- Name: SCHEMA public; Type: COMMENT; Schema: -; Owner: postgres
--

COMMENT ON SCHEMA public IS 'Standard public schema';


SET search_path = public, pg_catalog;

--
-- TOC entry 13 (class 1255 OID 19679)
-- Dependencies: 4 312
-- Name: nicestring_data_barang(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION nicestring_data_barang() RETURNS "trigger"
    AS $$BEGIN 
   NEW.nama := trim(lower(NEW.nama)); 
   NEW.kategori := trim(lower(NEW.kategori));
   return NEW; 
END;$$
    LANGUAGE plpgsql;


--
-- TOC entry 14 (class 1255 OID 19680)
-- Dependencies: 4 312
-- Name: nicestring_data_gudang(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION nicestring_data_gudang() RETURNS "trigger"
    AS $$BEGIN 
   NEW.nama := trim(lower(NEW.nama)); 
   NEW.alamat := trim(lower(NEW.alamat));
   return NEW; 
END;$$
    LANGUAGE plpgsql;


--
-- TOC entry 15 (class 1255 OID 19681)
-- Dependencies: 4 312
-- Name: nicestring_data_karyawan(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION nicestring_data_karyawan() RETURNS "trigger"
    AS $$BEGIN 
   NEW.nama_karyawan := trim(lower(NEW.nama_karyawan)); 
   NEW.posisi := trim(lower(NEW.posisi));
   NEW.alamat := trim(lower(NEW.alamat));
   NEW.tempat_lahir := trim(lower(NEW.tempat_lahir));
   return NEW; 
END;$$
    LANGUAGE plpgsql;


--
-- TOC entry 16 (class 1255 OID 19682)
-- Dependencies: 4 312
-- Name: nicestring_data_komisioner(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION nicestring_data_komisioner() RETURNS "trigger"
    AS $$BEGIN 
   NEW.nama_komisioner := trim(lower(NEW.nama_komisioner)); 
   NEW.alamat_komisioner := trim(lower(NEW.alamat_komisioner));
   return NEW; 
END;$$
    LANGUAGE plpgsql;


--
-- TOC entry 17 (class 1255 OID 19683)
-- Dependencies: 4 312
-- Name: nicestring_data_pembeli(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION nicestring_data_pembeli() RETURNS "trigger"
    AS $$BEGIN 
   NEW.nama := trim(lower(NEW.nama)); 
   NEW.alamat := trim(lower(NEW.alamat));
   NEW.telepon := trim(NEW.telepon);
   return NEW; 
END;$$
    LANGUAGE plpgsql;


--
-- TOC entry 18 (class 1255 OID 19684)
-- Dependencies: 4 312
-- Name: nicestring_data_produsen(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION nicestring_data_produsen() RETURNS "trigger"
    AS $$BEGIN
NEW.nama := trim(lower(NEW.nama));
NEW.alamat := trim(lower(NEW.alamat));
NEW.telepon := trim(NEW.telepon); 
return NEW; 
END;$$
    LANGUAGE plpgsql;


--
-- TOC entry 19 (class 1255 OID 19685)
-- Dependencies: 4 312
-- Name: nicestring_data_salesman(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION nicestring_data_salesman() RETURNS "trigger"
    AS $$BEGIN  
   NEW.nama_salesman := trim(lower(NEW.nama_salesman));
   NEW.alamat := trim(lower(NEW.alamat));
   NEW.tempat_lahir := trim(lower(NEW.tempat_lahir));
   return NEW; 
END;$$
    LANGUAGE plpgsql;


--
-- TOC entry 20 (class 1255 OID 19686)
-- Dependencies: 4 312
-- Name: nicestring_data_suplier(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION nicestring_data_suplier() RETURNS "trigger"
    AS $$BEGIN 
   NEW.nama_suplier := trim(lower(NEW.nama_suplier));
   NEW.alamat_suplier := trim(lower(NEW.alamat_suplier));
   NEW.telepon_suplier := trim(NEW.telepon_suplier);
   return NEW; 
END;$$
    LANGUAGE plpgsql;


--
-- TOC entry 21 (class 1255 OID 19687)
-- Dependencies: 4 312
-- Name: nicestring_data_transaksi_penjualan(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION nicestring_data_transaksi_penjualan() RETURNS "trigger"
    AS $$BEGIN
   NEW.pengirim := lower(trim(NEW.pengirim));
   return NEW;
END;$$
    LANGUAGE plpgsql;


--
-- TOC entry 22 (class 1255 OID 19688)
-- Dependencies: 4 312
-- Name: nicestring_nama(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION nicestring_nama() RETURNS "trigger"
    AS $$BEGIN 
   NEW.nama := trim(lower(NEW.nama)); 
   return NEW; 
END;$$
    LANGUAGE plpgsql;


--
-- TOC entry 11 (class 1255 OID 19674)
-- Dependencies: 4
-- Name: plpgsql_call_handler(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION plpgsql_call_handler() RETURNS language_handler
    AS '$libdir/plpgsql', 'plpgsql_call_handler'
    LANGUAGE c;


--
-- TOC entry 12 (class 1255 OID 19675)
-- Dependencies: 4
-- Name: plpgsql_validator(oid); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION plpgsql_validator(oid) RETURNS void
    AS '$libdir/plpgsql', 'plpgsql_validator'
    LANGUAGE c;


--
-- TOC entry 23 (class 1255 OID 19689)
-- Dependencies: 4 312
-- Name: text_comma_append(text, text); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION text_comma_append(text, text) RETURNS text
    AS $_$
BEGIN
        IF $1 IS NULL OR LENGTH($1) < 1 THEN
                RETURN COALESCE($2,'');
        ELSIF $2 IS NULL OR LENGTH($2) < 1 THEN
                RETURN $1;
        ELSE
                RETURN (COALESCE($1,'') || ', ' || COALESCE($2,''));
        END IF;
END;
$_$
    LANGUAGE plpgsql;


--
-- TOC entry 313 (class 1255 OID 19690)
-- Dependencies: 4
-- Name: array_accum(anyelement); Type: AGGREGATE; Schema: public; Owner: postgres
--

CREATE AGGREGATE array_accum (
    BASETYPE = anyelement,
    SFUNC = array_append,
    STYPE = anyarray,
    INITCOND = '{}'
);


--
-- TOC entry 314 (class 1255 OID 19691)
-- Dependencies: 4 23
-- Name: text_comma_agg(text); Type: AGGREGATE; Schema: public; Owner: postgres
--

CREATE AGGREGATE text_comma_agg (
    BASETYPE = text,
    SFUNC = text_comma_append,
    STYPE = text,
    INITCOND = ''
);


SET default_tablespace = '';

SET default_with_oids = true;

--
-- TOC entry 1248 (class 1259 OID 19692)
-- Dependencies: 1608 4
-- Name: data_barang; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE data_barang (
    "index" integer DEFAULT nextval(('kd_barang_seq'::text)::regclass) NOT NULL,
    nama character varying(200) NOT NULL,
    harga_modal integer NOT NULL,
    harga_jual integer NOT NULL,
    kd_barang character varying(200) NOT NULL,
    komentar text,
    kategori character varying(200),
    index_produsen integer NOT NULL,
    panjang integer,
    lebar integer,
    tinggi integer,
    volume integer,
    jumlah_ukuran integer,
    ukuran_primer character varying(200),
    ukuran_sekunder character varying(200),
    ukuran_ketiga character varying(200),
    jumlah_ukuran_dua_ke_tiga integer
);


--
-- TOC entry 1759 (class 0 OID 0)
-- Dependencies: 1248
-- Name: COLUMN data_barang.kd_barang; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN data_barang.kd_barang IS 'seperti barcode';


--
-- TOC entry 1249 (class 1259 OID 19698)
-- Dependencies: 1609 4
-- Name: data_gudang; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE data_gudang (
    alamat character varying(200) NOT NULL,
    komentar text,
    "index" integer DEFAULT nextval(('public.kd_gudang_seq'::text)::regclass) NOT NULL,
    nama character varying(200) NOT NULL
);


--
-- TOC entry 1250 (class 1259 OID 19704)
-- Dependencies: 1610 4
-- Name: data_karyawan; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE data_karyawan (
    nomor_induk character varying(200) NOT NULL,
    nama_karyawan character varying(200) NOT NULL,
    posisi character varying(200) NOT NULL,
    gaji integer NOT NULL,
    telepon character varying(200),
    alamat text NOT NULL,
    tempat_lahir character varying(200) NOT NULL,
    tanggal_lahir date NOT NULL,
    komentar text,
    "index" integer DEFAULT nextval(('public.index_karyawan_seq'::text)::regclass) NOT NULL,
    status boolean NOT NULL,
    tipe character varying(200) NOT NULL
);


--
-- TOC entry 1251 (class 1259 OID 19710)
-- Dependencies: 1611 4
-- Name: data_komisioner; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE data_komisioner (
    alamat_komisioner text,
    kd_komisioner integer DEFAULT nextval(('public.kd_komisioner_seq'::text)::regclass) NOT NULL,
    nama_komisioner character varying(200) NOT NULL,
    telepon_komisioner character varying(200),
    komentar text
);


--
-- TOC entry 1252 (class 1259 OID 19716)
-- Dependencies: 1612 1613 1614 4
-- Name: data_kontainer; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE data_kontainer (
    volume integer DEFAULT 0 NOT NULL,
    komentar text,
    index_kontainer integer DEFAULT nextval(('public.index_kontainer'::text)::regclass) NOT NULL,
    nama character varying(200) NOT NULL,
    harga bigint DEFAULT 0 NOT NULL
);


--
-- TOC entry 1253 (class 1259 OID 19724)
-- Dependencies: 1615 4
-- Name: data_pembeli; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE data_pembeli (
    komentar text,
    index_pembeli integer DEFAULT nextval(('public.index_pembeli'::text)::regclass) NOT NULL,
    nama character varying(200) NOT NULL,
    alamat character varying(200),
    telepon character varying(200)
);


--
-- TOC entry 1255 (class 1259 OID 19732)
-- Dependencies: 4
-- Name: data_produsen; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE data_produsen (
    "index" serial NOT NULL,
    nama character varying(200) NOT NULL,
    alamat character varying(200),
    telepon character varying(200),
    komentar text
);


--
-- TOC entry 1256 (class 1259 OID 19738)
-- Dependencies: 1617 4
-- Name: data_salesman; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE data_salesman (
    nomor_induk character varying(200) NOT NULL,
    nama_salesman character varying(200) NOT NULL,
    telepon character varying(200),
    alamat text NOT NULL,
    tempat_lahir character varying(200) NOT NULL,
    tanggal_lahir date NOT NULL,
    komentar text,
    index_salesman integer DEFAULT nextval(('public.index_salesman'::text)::regclass) NOT NULL,
    gaji integer,
    status boolean NOT NULL
);


--
-- TOC entry 1257 (class 1259 OID 19744)
-- Dependencies: 1618 4
-- Name: data_suplier; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE data_suplier (
    kd_suplier integer DEFAULT nextval(('kd_suplier_seq'::text)::regclass) NOT NULL,
    nama_suplier character varying(200) NOT NULL,
    alamat_suplier character varying(200),
    telepon_suplier character varying(20),
    komentar text
);


--
-- TOC entry 1258 (class 1259 OID 19750)
-- Dependencies: 1619 4
-- Name: data_transaksi_gaji; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE data_transaksi_gaji (
    invoice_gaji character varying(200) NOT NULL,
    tanggal_gaji date NOT NULL,
    komentar text,
    "index" integer DEFAULT nextval(('public.index_transaksi_gaji'::text)::regclass) NOT NULL
);


--
-- TOC entry 1259 (class 1259 OID 19756)
-- Dependencies: 1620 1621 1622 4
-- Name: data_transaksi_pembelian; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE data_transaksi_pembelian (
    tanggal date NOT NULL,
    potongan integer DEFAULT 0 NOT NULL,
    invoice character varying(200) NOT NULL,
    komisi_komisioner integer DEFAULT 0 NOT NULL,
    kd_komisioner integer,
    komentar text,
    "index" integer DEFAULT nextval(('public.index_transaksi_pembelian'::text)::regclass) NOT NULL,
    pengirim character varying(200),
    index_kontainer integer,
    suplier character varying(200)
);


--
-- TOC entry 1260 (class 1259 OID 19764)
-- Dependencies: 1623 1624 1625 1626 4
-- Name: data_transaksi_penjualan; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE data_transaksi_penjualan (
    tanggal date NOT NULL,
    potongan integer DEFAULT 0,
    invoice character varying(200) NOT NULL,
    komentar text,
    komisi_salesman integer DEFAULT 0,
    biaya_kirim integer DEFAULT 0,
    "index" integer DEFAULT nextval(('public.index_transaksi_penjualan'::text)::regclass) NOT NULL,
    pengirim character varying(200),
    nomor_salesman integer,
    nomor_pembeli integer,
    tertanggung_biaya_kirim character varying(200)
);


--
-- TOC entry 1262 (class 1259 OID 19776)
-- Dependencies: 4
-- Name: index_karyawan_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE index_karyawan_seq
    INCREMENT BY 1
    MAXVALUE 13000
    MINVALUE 0
    CACHE 1;


--
-- TOC entry 1263 (class 1259 OID 19778)
-- Dependencies: 4
-- Name: index_kontainer; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE index_kontainer
    INCREMENT BY 1
    NO MAXVALUE
    MINVALUE 0
    CACHE 1;


--
-- TOC entry 1264 (class 1259 OID 19780)
-- Dependencies: 4
-- Name: index_pembeli; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE index_pembeli
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


--
-- TOC entry 1265 (class 1259 OID 19782)
-- Dependencies: 4
-- Name: index_salesman; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE index_salesman
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


--
-- TOC entry 1266 (class 1259 OID 19784)
-- Dependencies: 4
-- Name: index_transaksi_gaji; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE index_transaksi_gaji
    INCREMENT BY 1
    MAXVALUE 13000
    MINVALUE 0
    CACHE 1;


--
-- TOC entry 1267 (class 1259 OID 19786)
-- Dependencies: 4
-- Name: index_transaksi_pembelian; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE index_transaksi_pembelian
    INCREMENT BY 1
    NO MAXVALUE
    MINVALUE 0
    CACHE 1;


--
-- TOC entry 1268 (class 1259 OID 19788)
-- Dependencies: 4
-- Name: index_transaksi_penjualan; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE index_transaksi_penjualan
    INCREMENT BY 1
    NO MAXVALUE
    MINVALUE 0
    CACHE 1;


--
-- TOC entry 1269 (class 1259 OID 19790)
-- Dependencies: 4
-- Name: kasih_barang_jual; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE kasih_barang_jual (
    index_penjualan integer NOT NULL,
    belum integer NOT NULL,
    sudah integer NOT NULL,
    index_barang integer NOT NULL
);


--
-- TOC entry 1270 (class 1259 OID 19792)
-- Dependencies: 4
-- Name: kd_barang_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE kd_barang_seq
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


--
-- TOC entry 1271 (class 1259 OID 19794)
-- Dependencies: 4
-- Name: kd_gudang_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE kd_gudang_seq
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


--
-- TOC entry 1272 (class 1259 OID 19796)
-- Dependencies: 4
-- Name: kd_komisioner_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE kd_komisioner_seq
    INCREMENT BY 1
    MAXVALUE 12000
    MINVALUE 0
    CACHE 1;


--
-- TOC entry 1273 (class 1259 OID 19798)
-- Dependencies: 4
-- Name: kd_suplier_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE kd_suplier_seq
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


--
-- TOC entry 1274 (class 1259 OID 19800)
-- Dependencies: 4
-- Name: kd_transaksi_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE kd_transaksi_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


--
-- TOC entry 1275 (class 1259 OID 19804)
-- Dependencies: 4
-- Name: piutang; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE piutang (
    index_penjualan integer NOT NULL,
    belum_bayar integer,
    sudah_bayar integer
);


--
-- TOC entry 1277 (class 1259 OID 19810)
-- Dependencies: 1630 1631 4
-- Name: stok_gudang; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE stok_gudang (
    jumlah integer DEFAULT 0 NOT NULL,
    index_barang integer NOT NULL,
    index_gudang integer NOT NULL,
    tanggal_kadaluarsa date,
    CONSTRAINT checked_positive_amount CHECK ((jumlah >= 0))
);


--
-- TOC entry 1278 (class 1259 OID 19814)
-- Dependencies: 4
-- Name: suplier_barang; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE suplier_barang (
    index_suplier integer NOT NULL,
    index_barang integer NOT NULL
);


--
-- TOC entry 1279 (class 1259 OID 19816)
-- Dependencies: 4
-- Name: terima_barang_beli; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE terima_barang_beli (
    index_barang integer NOT NULL,
    index_pembelian integer NOT NULL,
    belum integer NOT NULL,
    sudah integer NOT NULL
);


--
-- TOC entry 1261 (class 1259 OID 19773)
-- Dependencies: 1627 4
-- Name: transaksi_barang_pembelian; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE transaksi_barang_pembelian (
    index_barang integer NOT NULL,
    jumlah integer NOT NULL,
    harga_beli integer NOT NULL,
    diskon_per_barang integer DEFAULT 0 NOT NULL,
    index_pembelian integer NOT NULL
);


--
-- TOC entry 1276 (class 1259 OID 19806)
-- Dependencies: 1628 1629 4
-- Name: transaksi_barang_penjualan; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE transaksi_barang_penjualan (
    index_barang integer,
    diskon_per_barang integer DEFAULT 0,
    jumlah integer DEFAULT 0,
    harga_modal integer NOT NULL,
    harga_jual integer NOT NULL,
    index_penjualan integer NOT NULL
);


--
-- TOC entry 1280 (class 1259 OID 19818)
-- Dependencies: 1632 1633 4
-- Name: transaksi_gaji; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE transaksi_gaji (
    potongan integer DEFAULT 0 NOT NULL,
    index_karyawan integer NOT NULL,
    index_transaksi_gaji integer NOT NULL,
    tipe character varying(200) NOT NULL,
    gaji integer DEFAULT 0 NOT NULL
);


--
-- TOC entry 1285 (class 1259 OID 28428)
-- Dependencies: 1634 1635 4
-- Name: transaksi_gaji_salesman; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE transaksi_gaji_salesman (
    potongan integer DEFAULT 0 NOT NULL,
    index_salesman integer NOT NULL,
    index_transaksi_gaji integer NOT NULL,
    gaji integer DEFAULT 0 NOT NULL
);


--
-- TOC entry 1281 (class 1259 OID 19822)
-- Dependencies: 4
-- Name: transaksi_pemasukan_lain; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE transaksi_pemasukan_lain (
    invoice character varying(200) NOT NULL,
    tanggal date NOT NULL,
    deskripsi text NOT NULL,
    jumlah integer NOT NULL
);


--
-- TOC entry 1282 (class 1259 OID 19827)
-- Dependencies: 4
-- Name: transaksi_pengeluaran; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE transaksi_pengeluaran (
    invoice character varying(200) NOT NULL,
    tanggal date NOT NULL,
    nama character varying(200) NOT NULL,
    komentar text,
    jumlah integer NOT NULL
);


--
-- TOC entry 1283 (class 1259 OID 19832)
-- Dependencies: 4
-- Name: utang; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE utang (
    index_pembelian integer NOT NULL,
    belum_bayar integer,
    sudah_bayar integer
);


--
-- TOC entry 1284 (class 1259 OID 19834)
-- Dependencies: 1351 4
-- Name: view_general_salary; Type: VIEW; Schema: public; Owner: postgres
--

CREATE VIEW view_general_salary AS
    SELECT d.tanggal_gaji, d.invoice_gaji, d."index", sum((t.gaji - t.potongan)) AS value FROM (data_transaksi_gaji d JOIN transaksi_gaji t ON ((d."index" = t.index_transaksi_gaji))) GROUP BY d.invoice_gaji, d."index", d.tanggal_gaji;


--
-- TOC entry 1637 (class 2606 OID 19838)
-- Dependencies: 1248 1248
-- Name: data_barang_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY data_barang
    ADD CONSTRAINT data_barang_pkey PRIMARY KEY ("index");


--
-- TOC entry 1639 (class 2606 OID 20145)
-- Dependencies: 1248 1248
-- Name: data_barang_unique_kode_barang; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY data_barang
    ADD CONSTRAINT data_barang_unique_kode_barang UNIQUE (kd_barang);


--
-- TOC entry 1678 (class 2606 OID 19840)
-- Dependencies: 1257 1257
-- Name: data_suplier_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY data_suplier
    ADD CONSTRAINT data_suplier_pkey PRIMARY KEY (kd_suplier);


--
-- TOC entry 1680 (class 2606 OID 44989)
-- Dependencies: 1257 1257
-- Name: kd_suplier_unique; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY data_suplier
    ADD CONSTRAINT kd_suplier_unique UNIQUE (kd_suplier);


--
-- TOC entry 1642 (class 2606 OID 44994)
-- Dependencies: 1249 1249
-- Name: nama_gudang_unique; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY data_gudang
    ADD CONSTRAINT nama_gudang_unique UNIQUE (nama);


--
-- TOC entry 1654 (class 2606 OID 44996)
-- Dependencies: 1251 1251
-- Name: nama_komisioner_unique; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY data_komisioner
    ADD CONSTRAINT nama_komisioner_unique UNIQUE (nama_komisioner);


--
-- TOC entry 1658 (class 2606 OID 44998)
-- Dependencies: 1252 1252
-- Name: nama_kontainer_unique; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY data_kontainer
    ADD CONSTRAINT nama_kontainer_unique UNIQUE (nama);


--
-- TOC entry 1666 (class 2606 OID 45000)
-- Dependencies: 1255 1255
-- Name: nama_produsen_unique; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY data_produsen
    ADD CONSTRAINT nama_produsen_unique UNIQUE (nama);


--
-- TOC entry 1670 (class 2606 OID 45002)
-- Dependencies: 1256 1256
-- Name: nama_salesman_unique; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY data_salesman
    ADD CONSTRAINT nama_salesman_unique UNIQUE (nama_salesman);


--
-- TOC entry 1682 (class 2606 OID 45004)
-- Dependencies: 1257 1257
-- Name: nama_suplier_unique; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY data_suplier
    ADD CONSTRAINT nama_suplier_unique UNIQUE (nama_suplier);


--
-- TOC entry 1672 (class 2606 OID 44987)
-- Dependencies: 1256 1256
-- Name: nomor_induk_unique; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY data_salesman
    ADD CONSTRAINT nomor_induk_unique UNIQUE (nomor_induk);


--
-- TOC entry 1648 (class 2606 OID 19842)
-- Dependencies: 1250 1250
-- Name: primary_index_data_karyawan; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY data_karyawan
    ADD CONSTRAINT primary_index_data_karyawan PRIMARY KEY ("index");


--
-- TOC entry 1674 (class 2606 OID 19844)
-- Dependencies: 1256 1256
-- Name: primary_index_data_salesman; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY data_salesman
    ADD CONSTRAINT primary_index_data_salesman PRIMARY KEY (index_salesman);


--
-- TOC entry 1684 (class 2606 OID 19846)
-- Dependencies: 1258 1258
-- Name: primary_index_data_transaksi_gaji; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY data_transaksi_gaji
    ADD CONSTRAINT primary_index_data_transaksi_gaji PRIMARY KEY ("index");


--
-- TOC entry 1691 (class 2606 OID 19848)
-- Dependencies: 1259 1259
-- Name: primary_index_data_transaksi_pembelian; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY data_transaksi_pembelian
    ADD CONSTRAINT primary_index_data_transaksi_pembelian UNIQUE ("index");


--
-- TOC entry 1698 (class 2606 OID 19850)
-- Dependencies: 1260 1260
-- Name: primary_index_data_transaksi_penjualan; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY data_transaksi_penjualan
    ADD CONSTRAINT primary_index_data_transaksi_penjualan PRIMARY KEY ("index");


--
-- TOC entry 1644 (class 2606 OID 19852)
-- Dependencies: 1249 1249
-- Name: primary_index_gudang_barang; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY data_gudang
    ADD CONSTRAINT primary_index_gudang_barang PRIMARY KEY ("index");


--
-- TOC entry 1660 (class 2606 OID 19854)
-- Dependencies: 1252 1252
-- Name: primary_index_kontainer_data_kontainer; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY data_kontainer
    ADD CONSTRAINT primary_index_kontainer_data_kontainer PRIMARY KEY (index_kontainer);


--
-- TOC entry 1662 (class 2606 OID 19856)
-- Dependencies: 1253 1253
-- Name: primary_index_pembeli_data_pembeli; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY data_pembeli
    ADD CONSTRAINT primary_index_pembeli_data_pembeli PRIMARY KEY (index_pembeli);


--
-- TOC entry 1717 (class 2606 OID 19858)
-- Dependencies: 1281 1281
-- Name: primary_invoice_transaksi_pemasukan_lain; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY transaksi_pemasukan_lain
    ADD CONSTRAINT primary_invoice_transaksi_pemasukan_lain PRIMARY KEY (invoice);


--
-- TOC entry 1719 (class 2606 OID 19860)
-- Dependencies: 1282 1282
-- Name: primary_invoice_transaksi_pengeluaran; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY transaksi_pengeluaran
    ADD CONSTRAINT primary_invoice_transaksi_pengeluaran PRIMARY KEY (invoice);


--
-- TOC entry 1656 (class 2606 OID 19862)
-- Dependencies: 1251 1251
-- Name: primary_kd_komisioner; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY data_komisioner
    ADD CONSTRAINT primary_kd_komisioner PRIMARY KEY (kd_komisioner);


--
-- TOC entry 1668 (class 2606 OID 19864)
-- Dependencies: 1255 1255
-- Name: producer_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY data_produsen
    ADD CONSTRAINT producer_pkey PRIMARY KEY ("index");


--
-- TOC entry 1676 (class 2606 OID 44991)
-- Dependencies: 1256 1256
-- Name: salesman_nomor_induk_unique; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY data_salesman
    ADD CONSTRAINT salesman_nomor_induk_unique UNIQUE (nomor_induk);


--
-- TOC entry 1711 (class 2606 OID 20143)
-- Dependencies: 1277 1277 1277 1277
-- Name: stok_gudang_unique; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY stok_gudang
    ADD CONSTRAINT stok_gudang_unique UNIQUE (index_barang, index_gudang, tanggal_kadaluarsa);


--
-- TOC entry 1693 (class 2606 OID 19866)
-- Dependencies: 1259 1259
-- Name: unique_index; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY data_transaksi_pembelian
    ADD CONSTRAINT unique_index UNIQUE ("index");


--
-- TOC entry 1646 (class 2606 OID 19868)
-- Dependencies: 1249 1249
-- Name: unique_index_gudang_barang; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY data_gudang
    ADD CONSTRAINT unique_index_gudang_barang UNIQUE ("index");


--
-- TOC entry 1650 (class 2606 OID 19870)
-- Dependencies: 1250 1250
-- Name: unique_index_karyawan; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY data_karyawan
    ADD CONSTRAINT unique_index_karyawan UNIQUE ("index");


--
-- TOC entry 1721 (class 2606 OID 19872)
-- Dependencies: 1283 1283
-- Name: unique_index_pembelian_utang; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY utang
    ADD CONSTRAINT unique_index_pembelian_utang UNIQUE (index_pembelian);


--
-- TOC entry 1700 (class 2606 OID 19874)
-- Dependencies: 1260 1260
-- Name: unique_index_penjualan; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY data_transaksi_penjualan
    ADD CONSTRAINT unique_index_penjualan UNIQUE ("index");


--
-- TOC entry 1706 (class 2606 OID 19876)
-- Dependencies: 1275 1275
-- Name: unique_index_penjualan_piutang; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY piutang
    ADD CONSTRAINT unique_index_penjualan_piutang UNIQUE (index_penjualan);


--
-- TOC entry 1686 (class 2606 OID 19878)
-- Dependencies: 1258 1258
-- Name: unique_index_transaksi_gaji; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY data_transaksi_gaji
    ADD CONSTRAINT unique_index_transaksi_gaji UNIQUE ("index");


--
-- TOC entry 1695 (class 2606 OID 19880)
-- Dependencies: 1259 1259
-- Name: unique_invoice_data_transaksi_pembelian; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY data_transaksi_pembelian
    ADD CONSTRAINT unique_invoice_data_transaksi_pembelian UNIQUE (invoice);


--
-- TOC entry 1702 (class 2606 OID 19882)
-- Dependencies: 1260 1260
-- Name: unique_invoice_data_transaksi_penjualan; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY data_transaksi_penjualan
    ADD CONSTRAINT unique_invoice_data_transaksi_penjualan UNIQUE (invoice);


--
-- TOC entry 1688 (class 2606 OID 19884)
-- Dependencies: 1258 1258
-- Name: unique_invoice_gaji_data_transaksi_gaji; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY data_transaksi_gaji
    ADD CONSTRAINT unique_invoice_gaji_data_transaksi_gaji UNIQUE (invoice_gaji);


--
-- TOC entry 1664 (class 2606 OID 19886)
-- Dependencies: 1253 1253
-- Name: unique_nama_data_pembeli; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY data_pembeli
    ADD CONSTRAINT unique_nama_data_pembeli UNIQUE (nama);


--
-- TOC entry 1652 (class 2606 OID 19888)
-- Dependencies: 1250 1250
-- Name: unique_nomor_induk_karyawan; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY data_karyawan
    ADD CONSTRAINT unique_nomor_induk_karyawan UNIQUE (nomor_induk);


--
-- TOC entry 1640 (class 1259 OID 19889)
-- Dependencies: 1248
-- Name: fki_data_barang_producer_fkey; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX fki_data_barang_producer_fkey ON data_barang USING btree (index_produsen);


--
-- TOC entry 1708 (class 1259 OID 19890)
-- Dependencies: 1277
-- Name: fki_foreign_data_gudang; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX fki_foreign_data_gudang ON stok_gudang USING btree (index_gudang);


--
-- TOC entry 1704 (class 1259 OID 19891)
-- Dependencies: 1269
-- Name: fki_foreign_index_barang_kasih_barang_jual; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX fki_foreign_index_barang_kasih_barang_jual ON kasih_barang_jual USING btree (index_barang);


--
-- TOC entry 1709 (class 1259 OID 19892)
-- Dependencies: 1277
-- Name: fki_foreign_index_barang_stok_gudang; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX fki_foreign_index_barang_stok_gudang ON stok_gudang USING btree (index_barang);


--
-- TOC entry 1714 (class 1259 OID 19893)
-- Dependencies: 1280
-- Name: fki_foreign_index_karyawan; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX fki_foreign_index_karyawan ON transaksi_gaji USING btree (index_karyawan);


--
-- TOC entry 1689 (class 1259 OID 19894)
-- Dependencies: 1259
-- Name: fki_foreign_index_kontainer_data_kontainer; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX fki_foreign_index_kontainer_data_kontainer ON data_transaksi_pembelian USING btree (index_kontainer);


--
-- TOC entry 1703 (class 1259 OID 19895)
-- Dependencies: 1261
-- Name: fki_foreign_index_pembelian; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX fki_foreign_index_pembelian ON transaksi_barang_pembelian USING btree (index_pembelian);


--
-- TOC entry 1707 (class 1259 OID 19896)
-- Dependencies: 1276
-- Name: fki_foreign_index_penjualan; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX fki_foreign_index_penjualan ON transaksi_barang_penjualan USING btree (index_penjualan);


--
-- TOC entry 1715 (class 1259 OID 19897)
-- Dependencies: 1280
-- Name: fki_foreign_index_transaksi_gaji; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX fki_foreign_index_transaksi_gaji ON transaksi_gaji USING btree (index_transaksi_gaji);


--
-- TOC entry 1696 (class 1259 OID 19898)
-- Dependencies: 1260
-- Name: fki_foreign_nomor_salesman_data_transaksi_penjualan; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX fki_foreign_nomor_salesman_data_transaksi_penjualan ON data_transaksi_penjualan USING btree (nomor_salesman);


--
-- TOC entry 1712 (class 1259 OID 19899)
-- Dependencies: 1278
-- Name: fki_suplier_barang_index_barang_fkey; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX fki_suplier_barang_index_barang_fkey ON suplier_barang USING btree (index_barang);


--
-- TOC entry 1713 (class 1259 OID 19900)
-- Dependencies: 1278
-- Name: fki_suplier_barang_index_suplier_fkey; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX fki_suplier_barang_index_suplier_fkey ON suplier_barang USING btree (index_suplier);


--
-- TOC entry 1753 (class 2620 OID 19901)
-- Dependencies: 20 1257
-- Name: trigger_data_barang; Type: TRIGGER; Schema: public; Owner: postgres
--

CREATE TRIGGER trigger_data_barang
    BEFORE INSERT OR UPDATE ON data_suplier
    FOR EACH ROW
    EXECUTE PROCEDURE nicestring_data_suplier();


--
-- TOC entry 1747 (class 2620 OID 19902)
-- Dependencies: 15 1250
-- Name: trigger_data_karyawan; Type: TRIGGER; Schema: public; Owner: postgres
--

CREATE TRIGGER trigger_data_karyawan
    BEFORE INSERT OR UPDATE ON data_karyawan
    FOR EACH ROW
    EXECUTE PROCEDURE nicestring_data_karyawan();


--
-- TOC entry 1748 (class 2620 OID 19903)
-- Dependencies: 16 1251
-- Name: trigger_data_komisioner; Type: TRIGGER; Schema: public; Owner: postgres
--

CREATE TRIGGER trigger_data_komisioner
    BEFORE INSERT OR UPDATE ON data_komisioner
    FOR EACH ROW
    EXECUTE PROCEDURE nicestring_data_komisioner();


--
-- TOC entry 1749 (class 2620 OID 19904)
-- Dependencies: 22 1252
-- Name: trigger_data_kontainer; Type: TRIGGER; Schema: public; Owner: postgres
--

CREATE TRIGGER trigger_data_kontainer
    BEFORE INSERT OR UPDATE ON data_kontainer
    FOR EACH ROW
    EXECUTE PROCEDURE nicestring_nama();


--
-- TOC entry 1750 (class 2620 OID 19905)
-- Dependencies: 17 1253
-- Name: trigger_data_pembeli; Type: TRIGGER; Schema: public; Owner: postgres
--

CREATE TRIGGER trigger_data_pembeli
    BEFORE INSERT OR UPDATE ON data_pembeli
    FOR EACH ROW
    EXECUTE PROCEDURE nicestring_data_pembeli();


--
-- TOC entry 1751 (class 2620 OID 19906)
-- Dependencies: 18 1255
-- Name: trigger_data_produsen; Type: TRIGGER; Schema: public; Owner: postgres
--

CREATE TRIGGER trigger_data_produsen
    BEFORE INSERT OR UPDATE ON data_produsen
    FOR EACH ROW
    EXECUTE PROCEDURE nicestring_data_produsen();


--
-- TOC entry 1752 (class 2620 OID 19907)
-- Dependencies: 19 1256
-- Name: trigger_data_salesman; Type: TRIGGER; Schema: public; Owner: postgres
--

CREATE TRIGGER trigger_data_salesman
    BEFORE INSERT OR UPDATE ON data_salesman
    FOR EACH ROW
    EXECUTE PROCEDURE nicestring_data_salesman();


--
-- TOC entry 1745 (class 2620 OID 19908)
-- Dependencies: 13 1248
-- Name: trigger_nicestring_data_barang; Type: TRIGGER; Schema: public; Owner: postgres
--

CREATE TRIGGER trigger_nicestring_data_barang
    BEFORE INSERT OR UPDATE ON data_barang
    FOR EACH ROW
    EXECUTE PROCEDURE nicestring_data_barang();


--
-- TOC entry 1746 (class 2620 OID 19909)
-- Dependencies: 14 1249
-- Name: trigger_nicestring_data_gudang; Type: TRIGGER; Schema: public; Owner: postgres
--

CREATE TRIGGER trigger_nicestring_data_gudang
    BEFORE INSERT OR UPDATE ON data_gudang
    FOR EACH ROW
    EXECUTE PROCEDURE nicestring_data_gudang();


--
-- TOC entry 1754 (class 2620 OID 19910)
-- Dependencies: 21 1260
-- Name: trigger_nicestring_data_transaksi_penjualan; Type: TRIGGER; Schema: public; Owner: postgres
--

CREATE TRIGGER trigger_nicestring_data_transaksi_penjualan
    BEFORE INSERT OR UPDATE ON data_transaksi_penjualan
    FOR EACH ROW
    EXECUTE PROCEDURE nicestring_data_transaksi_penjualan();


--
-- TOC entry 1722 (class 2606 OID 19911)
-- Dependencies: 1248 1255 1667
-- Name: data_barang_producer_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY data_barang
    ADD CONSTRAINT data_barang_producer_fkey FOREIGN KEY (index_produsen) REFERENCES data_produsen("index") ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 1734 (class 2606 OID 19916)
-- Dependencies: 1277 1249 1643
-- Name: foreign_data_gudang; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY stok_gudang
    ADD CONSTRAINT foreign_data_gudang FOREIGN KEY (index_gudang) REFERENCES data_gudang("index") ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 1729 (class 2606 OID 19921)
-- Dependencies: 1269 1248 1636
-- Name: foreign_index_barang_kasih_barang_jual; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY kasih_barang_jual
    ADD CONSTRAINT foreign_index_barang_kasih_barang_jual FOREIGN KEY (index_barang) REFERENCES data_barang("index") ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 1735 (class 2606 OID 19926)
-- Dependencies: 1277 1248 1636
-- Name: foreign_index_barang_stok_gudang; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY stok_gudang
    ADD CONSTRAINT foreign_index_barang_stok_gudang FOREIGN KEY (index_barang) REFERENCES data_barang("index") ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 1738 (class 2606 OID 19931)
-- Dependencies: 1279 1248 1636
-- Name: foreign_index_barang_terima; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY terima_barang_beli
    ADD CONSTRAINT foreign_index_barang_terima FOREIGN KEY (index_barang) REFERENCES data_barang("index") ON UPDATE RESTRICT ON DELETE CASCADE;


--
-- TOC entry 1740 (class 2606 OID 19936)
-- Dependencies: 1280 1250 1647
-- Name: foreign_index_karyawan; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY transaksi_gaji
    ADD CONSTRAINT foreign_index_karyawan FOREIGN KEY (index_karyawan) REFERENCES data_karyawan("index") ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 1723 (class 2606 OID 19941)
-- Dependencies: 1259 1252 1659
-- Name: foreign_index_kontainer_data_kontainer; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY data_transaksi_pembelian
    ADD CONSTRAINT foreign_index_kontainer_data_kontainer FOREIGN KEY (index_kontainer) REFERENCES data_kontainer(index_kontainer) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 1727 (class 2606 OID 19946)
-- Dependencies: 1261 1259 1690
-- Name: foreign_index_pembelian; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY transaksi_barang_pembelian
    ADD CONSTRAINT foreign_index_pembelian FOREIGN KEY (index_pembelian) REFERENCES data_transaksi_pembelian("index") ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 1742 (class 2606 OID 19951)
-- Dependencies: 1283 1259 1690
-- Name: foreign_index_pembelian; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY utang
    ADD CONSTRAINT foreign_index_pembelian FOREIGN KEY (index_pembelian) REFERENCES data_transaksi_pembelian("index") ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 1739 (class 2606 OID 19956)
-- Dependencies: 1279 1259 1690
-- Name: foreign_index_pembelian_terima_beli; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY terima_barang_beli
    ADD CONSTRAINT foreign_index_pembelian_terima_beli FOREIGN KEY (index_pembelian) REFERENCES data_transaksi_pembelian("index") ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 1732 (class 2606 OID 19961)
-- Dependencies: 1276 1260 1697
-- Name: foreign_index_penjualan; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY transaksi_barang_penjualan
    ADD CONSTRAINT foreign_index_penjualan FOREIGN KEY (index_penjualan) REFERENCES data_transaksi_penjualan("index") ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 1731 (class 2606 OID 19966)
-- Dependencies: 1275 1260 1697
-- Name: foreign_index_penjualan; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY piutang
    ADD CONSTRAINT foreign_index_penjualan FOREIGN KEY (index_penjualan) REFERENCES data_transaksi_penjualan("index") ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 1730 (class 2606 OID 19971)
-- Dependencies: 1269 1260 1697
-- Name: foreign_index_penjualan_belum_kasih; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY kasih_barang_jual
    ADD CONSTRAINT foreign_index_penjualan_belum_kasih FOREIGN KEY (index_penjualan) REFERENCES data_transaksi_penjualan("index") ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 1743 (class 2606 OID 28432)
-- Dependencies: 1285 1256 1673
-- Name: foreign_index_salesman; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY transaksi_gaji_salesman
    ADD CONSTRAINT foreign_index_salesman FOREIGN KEY (index_salesman) REFERENCES data_salesman(index_salesman) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 1741 (class 2606 OID 19976)
-- Dependencies: 1280 1258 1683
-- Name: foreign_index_transaksi_gaji; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY transaksi_gaji
    ADD CONSTRAINT foreign_index_transaksi_gaji FOREIGN KEY (index_transaksi_gaji) REFERENCES data_transaksi_gaji("index") ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 1744 (class 2606 OID 28437)
-- Dependencies: 1285 1258 1683
-- Name: foreign_index_transaksi_gaji; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY transaksi_gaji_salesman
    ADD CONSTRAINT foreign_index_transaksi_gaji FOREIGN KEY (index_transaksi_gaji) REFERENCES data_transaksi_gaji("index") ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 1724 (class 2606 OID 19981)
-- Dependencies: 1259 1251 1655
-- Name: foreign_kd_komisioner_data_transaksi_pembelian; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY data_transaksi_pembelian
    ADD CONSTRAINT foreign_kd_komisioner_data_transaksi_pembelian FOREIGN KEY (kd_komisioner) REFERENCES data_komisioner(kd_komisioner);


--
-- TOC entry 1725 (class 2606 OID 19986)
-- Dependencies: 1260 1253 1661
-- Name: foreign_nomor_pembeli_data_transaksi_penjualan; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY data_transaksi_penjualan
    ADD CONSTRAINT foreign_nomor_pembeli_data_transaksi_penjualan FOREIGN KEY (nomor_pembeli) REFERENCES data_pembeli(index_pembeli) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 1726 (class 2606 OID 19991)
-- Dependencies: 1260 1256 1673
-- Name: foreign_nomor_salesman_data_transaksi_penjualan; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY data_transaksi_penjualan
    ADD CONSTRAINT foreign_nomor_salesman_data_transaksi_penjualan FOREIGN KEY (nomor_salesman) REFERENCES data_salesman(index_salesman) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 1736 (class 2606 OID 19996)
-- Dependencies: 1278 1248 1636
-- Name: suplier_barang_index_barang_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY suplier_barang
    ADD CONSTRAINT suplier_barang_index_barang_fkey FOREIGN KEY (index_barang) REFERENCES data_barang("index") ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 1737 (class 2606 OID 20001)
-- Dependencies: 1278 1257 1677
-- Name: suplier_barang_index_suplier_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY suplier_barang
    ADD CONSTRAINT suplier_barang_index_suplier_fkey FOREIGN KEY (index_suplier) REFERENCES data_suplier(kd_suplier) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 1733 (class 2606 OID 20006)
-- Dependencies: 1276 1248 1636
-- Name: transaksi_barang_kd_barang_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY transaksi_barang_penjualan
    ADD CONSTRAINT transaksi_barang_kd_barang_fkey FOREIGN KEY (index_barang) REFERENCES data_barang("index");


--
-- TOC entry 1728 (class 2606 OID 20011)
-- Dependencies: 1261 1248 1636
-- Name: transaksi_barang_pembelian_index_barang_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY transaksi_barang_pembelian
    ADD CONSTRAINT transaksi_barang_pembelian_index_barang_fkey FOREIGN KEY (index_barang) REFERENCES data_barang("index");


--
-- TOC entry 1758 (class 0 OID 0)
-- Dependencies: 4
-- Name: public; Type: ACL; Schema: -; Owner: postgres
--

REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM postgres;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO PUBLIC;


-- Completed on 2007-03-25 01:04:30 WIT

--
-- PostgreSQL database dump complete
--

