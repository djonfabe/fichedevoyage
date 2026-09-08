-- V3 Seed compagnies aeriennes (compatible H2 + PostgreSQL)
MERGE INTO compagnies (nom, code_iata, code_icao, active)
KEY(nom) VALUES
('Turkish Airlines',    'TK', 'THY', TRUE),
('Tunis Air',           'TU', 'TAR', TRUE),
('Royal Air Maroc',     'AT', 'RAM', TRUE),
('Air Cote d Ivoire',   'HF', 'VRE', TRUE),
('Toumai Air Tchad',    'T7', 'TMJ', TRUE),
('Air Senegal',         'HC', 'ASL', TRUE),
('ASKY Airlines',       'KP', 'KPA', TRUE),
('Ethiopian Airlines',  'ET', 'ETH', TRUE),
('Brussels Airlines',   'SN', 'BEL', TRUE),
('Air Algerie',         'AH', 'DAH', TRUE),
('Africa World Airlines','AW','GWA', TRUE),
('Kenya Airways',       'KQ', 'KQA', TRUE),
('Air France',          'AF', 'AFR', TRUE),
('Emirates',            'EK', 'UAE', TRUE),
('Qatar Airways',       'QR', 'QTR', TRUE),
('Autre Compagnie',     NULL, NULL,   TRUE);
