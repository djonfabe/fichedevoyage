-- ============================================================
-- V1 Schema initial — compatible H2 (PostgreSQL mode) + PostgreSQL
-- ============================================================

CREATE TABLE IF NOT EXISTS pays (
    id            BIGSERIAL    PRIMARY KEY,
    code_iso2     VARCHAR(2)   NOT NULL UNIQUE,
    code_iso3     VARCHAR(3)   NOT NULL UNIQUE,
    nom_fr        VARCHAR(100) NOT NULL,
    nom_en        VARCHAR(100) NOT NULL,
    indicatif_tel VARCHAR(10)
);

CREATE TABLE IF NOT EXISTS compagnies (
    id         BIGSERIAL    PRIMARY KEY,
    nom        VARCHAR(100) NOT NULL,
    code_iata  VARCHAR(2),
    code_icao  VARCHAR(3),
    active     BOOLEAN      NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS vols (
    id               BIGSERIAL    PRIMARY KEY,
    compagnie_id     BIGINT       NOT NULL REFERENCES compagnies(id),
    numero           VARCHAR(20)  NOT NULL,
    jours_operation  VARCHAR(7),
    heure_arrivee    TIME,
    heure_depart     TIME,
    active           BOOLEAN      NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS users (
    id                    BIGSERIAL    PRIMARY KEY,
    email                 VARCHAR(150) NOT NULL UNIQUE,
    password_hash         VARCHAR(255) NOT NULL,
    nom                   VARCHAR(100) NOT NULL,
    prenoms               VARCHAR(150) NOT NULL,
    role                  VARCHAR(20)  NOT NULL CHECK (role IN ('ADMIN','SUPERVISEUR','AGENT_FRONTIERE')),
    totp_secret           VARCHAR(255),
    totp_enabled          BOOLEAN      NOT NULL DEFAULT FALSE,
    poste_frontiere_id    BIGINT,
    active                BOOLEAN      NOT NULL DEFAULT TRUE,
    tentatives_echecs     INT          NOT NULL DEFAULT 0,
    verrouille_jusqu_au   TIMESTAMP,
    created_at            TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_login_at         TIMESTAMP
);

CREATE TABLE IF NOT EXISTS publicites (
    id                  BIGSERIAL    PRIMARY KEY,
    titre               VARCHAR(200) NOT NULL,
    image_url           VARCHAR(500) NOT NULL,
    lien_cible          VARCHAR(500),
    position            VARCHAR(20)  NOT NULL CHECK (position IN ('ACCUEIL','ETAPES','FOOTER')),
    date_debut          DATE,
    date_fin            DATE,
    nombre_impressions  BIGINT       NOT NULL DEFAULT 0,
    nombre_clics        BIGINT       NOT NULL DEFAULT 0,
    active              BOOLEAN      NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS fiches_voyage (
    id                   UUID         DEFAULT RANDOM_UUID() PRIMARY KEY,
    reference            VARCHAR(20)  NOT NULL UNIQUE,
    qr_code_token        VARCHAR(64)  NOT NULL UNIQUE,

    nom                  VARCHAR(100) NOT NULL,
    prenoms              VARCHAR(150) NOT NULL,
    nom_jeune_fille      VARCHAR(100),
    sexe                 VARCHAR(10)  NOT NULL CHECK (sexe IN ('MASCULIN','FEMININ')),
    date_naissance       DATE         NOT NULL,
    lieu_naissance       VARCHAR(150) NOT NULL,
    email                VARCHAR(150),
    profession           VARCHAR(150) NOT NULL,
    fonction             VARCHAR(150),
    nationalite_id       BIGINT       NOT NULL REFERENCES pays(id),
    pays_residence_id    BIGINT       NOT NULL REFERENCES pays(id),

    type_document        VARCHAR(20)  NOT NULL CHECK (type_document IN ('PASSPORT','CNI','LAISSEZ_PASSER')),
    numero_document      VARCHAR(50)  NOT NULL,
    date_delivrance      DATE         NOT NULL,
    lieu_delivrance      VARCHAR(150) NOT NULL,

    pays_provenance_id   BIGINT REFERENCES pays(id),
    ville_provenance     VARCHAR(100),
    adresse_provenance   VARCHAR(255),
    contact_provenance   VARCHAR(25),

    pays_destination_id  BIGINT REFERENCES pays(id),
    ville_destination    VARCHAR(100),
    adresse_destination  VARCHAR(255),
    contact_destination  VARCHAR(25),

    date_voyage          DATE         NOT NULL,
    motif_voyage         VARCHAR(20)  CHECK (motif_voyage IN ('SANTE','CONFERENCE','RELIGION','FAMILLE','TRANSIT','AFFAIRES','VACANCES','ETUDES','AUTRE')),
    duree_sejour_jours   INT,
    type_voyage          VARCHAR(10)  CHECK (type_voyage IN ('ENTREE','SORTIE','TRANSIT')),
    type_hebergement     VARCHAR(20)  CHECK (type_hebergement IN ('HOTEL','FAMILLE','AMIS','RESIDENCE_PRIVEE')),

    compagnie_id         BIGINT REFERENCES compagnies(id),
    numero_vol           VARCHAR(20),

    engagement_accepte   BOOLEAN      NOT NULL,
    ip_soumission        VARCHAR(45),
    user_agent           TEXT,
    statut               VARCHAR(15)  NOT NULL DEFAULT 'BROUILLON' CHECK (statut IN ('BROUILLON','VALIDEE','SCANNEE','ANNULEE')),
    scanne_par_agent_id  BIGINT REFERENCES users(id),
    scanne_le            TIMESTAMP,
    created_at           TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at           TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_fiche_qr_token    ON fiches_voyage(qr_code_token);
CREATE INDEX IF NOT EXISTS idx_fiche_numero_doc  ON fiches_voyage(numero_document);
CREATE INDEX IF NOT EXISTS idx_fiche_date_voyage ON fiches_voyage(date_voyage);

CREATE TABLE IF NOT EXISTS audit_logs (
    id            BIGSERIAL    PRIMARY KEY,
    user_id       BIGINT,
    action        VARCHAR(100) NOT NULL,
    entite        VARCHAR(100),
    entite_id     VARCHAR(100),
    donnees_avant TEXT,
    donnees_apres TEXT,
    ip            VARCHAR(45),
    user_agent    TEXT,
    created_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);
