-- Add index on statut column for faster query performance on fiches_voyage status counts and filtering
CREATE INDEX IF NOT EXISTS idx_fiche_statut ON fiches_voyage(statut);
