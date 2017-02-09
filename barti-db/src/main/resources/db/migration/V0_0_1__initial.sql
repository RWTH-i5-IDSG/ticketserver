CREATE TABLE public.betreiber_key
(
  chr bytea NOT NULL,
  private_exponent bytea NOT NULL,
  modulus bytea NOT NULL,
  CONSTRAINT pk_betreiber_key PRIMARY KEY (chr)
);
COMMENT ON COLUMN public.betreiber_key.chr IS 'Certificate Holder Reference';

CREATE TABLE public.organisation
(
  org_id integer NOT NULL,
  name character varying NOT NULL,
  CONSTRAINT pk_org_id PRIMARY KEY (org_id)
);

CREATE TABLE public.product
(
  product_id integer NOT NULL,
  pv_org_id integer NOT NULL,
  means_of_transport_category_code integer NOT NULL,
  CONSTRAINT pk_product PRIMARY KEY (product_id, pv_org_id),
  CONSTRAINT fk_pv_org_id FOREIGN KEY (pv_org_id) REFERENCES public.organisation (org_id) ON UPDATE NO ACTION ON DELETE NO ACTION
);

CREATE TABLE public.partner
(
  partner_id integer NOT NULL,
  name character varying NOT NULL,
  CONSTRAINT pk_partner PRIMARY KEY (partner_id)
);

CREATE TABLE public.ticket_api_token
(
  api_token character varying NOT NULL,
  kvp_org_id integer NOT NULL,
  pv_org_id integer NOT NULL,
  product_id integer NOT NULL,
  partner_id integer NOT NULL,
  active boolean NOT NULL DEFAULT TRUE,
  CONSTRAINT pk_ticket_api_token PRIMARY KEY (api_token),
  CONSTRAINT fk_product FOREIGN KEY (product_id, pv_org_id) REFERENCES public.product (product_id, pv_org_id) ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_kvp FOREIGN KEY (kvp_org_id) REFERENCES public.organisation (org_id)  ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_partner FOREIGN KEY (partner_id) REFERENCES public.partner (partner_id) ON UPDATE NO ACTION ON DELETE NO ACTION
);

CREATE TABLE public.log_api_token
(
  api_token character varying NOT NULL,
  pv_org_id integer NOT NULL,
  active boolean NOT NULL DEFAULT TRUE,
  CONSTRAINT pk_log_api_token PRIMARY KEY (api_token),
  CONSTRAINT fk_product FOREIGN KEY (pv_org_id) REFERENCES public.organisation (org_id) ON UPDATE NO ACTION ON DELETE NO ACTION
);

CREATE TABLE public.transaction_data
(
  transaction_data_id integer NOT NULL,
  transaction_operator_id integer NOT NULL,
  terminal_number integer NOT NULL,
  terminal_org_id integer NOT NULL,
  location_number integer NOT NULL,
  location_org_id integer NOT NULL,
  CONSTRAINT pk_transaction_data PRIMARY KEY (transaction_data_id),
  CONSTRAINT fk_transaction_operator_id FOREIGN KEY (transaction_operator_id) REFERENCES public.organisation (org_id) ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_terminal_org_id FOREIGN KEY (terminal_org_id) REFERENCES public.organisation (org_id) ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_location_org_id FOREIGN KEY (location_org_id) REFERENCES public.organisation (org_id) ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT uq_transaction_data UNIQUE (transaction_operator_id, terminal_number, terminal_org_id, location_number, location_org_id)
);

CREATE TABLE public.deployment
(
  deployment_id integer NOT NULL,
  CONSTRAINT pk_deployment PRIMARY KEY (deployment_id)
);

CREATE TABLE public.deployment_product_to_transaction_data
(
  product_id integer NOT NULL,
  pv_org_id integer NOT NULL,
  deployment_id integer NOT NULL,
  transaction_data_id integer NOT NULL,
  CONSTRAINT pk_deployment_product_to_transaction_data PRIMARY KEY (product_id, pv_org_id, deployment_id, transaction_data_id),
  CONSTRAINT fk_product FOREIGN KEY (product_id, pv_org_id) REFERENCES public.product (product_id, pv_org_id) ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_deployment FOREIGN KEY (deployment_id) REFERENCES public.deployment (deployment_id) ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_transaction_data FOREIGN KEY (transaction_data_id) REFERENCES public.transaction_data (transaction_data_id) ON UPDATE NO ACTION ON DELETE NO ACTION
);

CREATE TABLE public.sequence_information
(
  kvp_org_id integer NOT NULL,
  deployment_id integer NOT NULL,
  current_value bigint NOT NULL,
  min_value bigint NOT NULL,
  max_value bigint NOT NULL,
  CONSTRAINT pk_sequences PRIMARY KEY (kvp_org_id, deployment_id),
  CONSTRAINT fk_kvp FOREIGN KEY (kvp_org_id) REFERENCES public.organisation (org_id) ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_deployment FOREIGN KEY (deployment_id) REFERENCES public.deployment (deployment_id) ON UPDATE NO ACTION ON DELETE NO ACTION
);

CREATE TABLE public.tickets_created
(
  logtime timestamp without time zone NOT NULL,
  -- interface parameters
  bov timestamp without time zone NOT NULL,
  eov timestamp without time zone NOT NULL,
  freitext character varying NOT NULL,
  -- product configuration
  api_token character varying NOT NULL,
  partner_id integer NOT NULL,
  partner_name character varying NOT NULL,
  deployment_id integer NOT NULL,
  product_id integer NOT NULL,
  pv_org_id integer NOT NULL,
  kvp_org_id integer NOT NULL,
  transaction_op_org_id integer NOT NULL,
  terminal_org_id integer NOT NULL,
  terminal_number integer NOT NULL,
  location_org_id integer NOT NULL,
  location_number integer NOT NULL,
  means_of_transport_category_code integer NOT NULL,
  -- ticket instance specific information
  ticket_number bigint NOT NULL,
  -- sam specific information
  pv_mk_version integer NOT NULL,
  sam_id character varying NOT NULL,
  sign_key_chr bytea NOT NULL,
  -- resulting ticket
  ticket bytea NOT NULL
);

CREATE TABLE public.operations_log
(
  logtime timestamp without time zone NOT NULL,
  deployment integer NOT NULL,
  level character varying NOT NULL,
  logger character varying NOT NULL,
  message character varying NOT NULL,
  throwable character varying NOT NULL
);

CREATE TABLE public.last_update
(
  logtime timestamp without time zone NOT NULL,
  deployment integer NOT NULL
);

CREATE OR REPLACE FUNCTION public.initialize_deployments_and_sequence_information(num_deployments integer DEFAULT 4)
  RETURNS integer AS
$BODY$
DECLARE
  max_four_bytes_value bigint := 4294967295;
BEGIN
  -- TRUNCATE deployment, sequence_information, deployment_product_to_transaction_data;
  FOR i IN 1..num_deployments LOOP
    INSERT INTO deployment (deployment_id) VALUES (i);
    INSERT INTO last_update (logtime, deployment) VALUES (now(), i);
  END LOOP;
  DECLARE
    kvp integer;
  BEGIN
    FOR kvp IN SELECT DISTINCT kvp_org_id FROM ticket_api_token LOOP
      FOR i IN 1..num_deployments LOOP
        DECLARE
          step bigint := (1 + max_four_bytes_value) / num_deployments;
          min_val bigint := (i - 1) * step;
          max_val bigint := i * step - 1;
        BEGIN
          INSERT INTO sequence_information
            (kvp_org_id, deployment_id, current_value, min_value, max_value)
          VALUES
            (kvp, i, min_val, min_val, max_val);
        END;
      END LOOP;
    END LOOP;
  END;
  return 0;
END;
$BODY$
LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION public.use_transaction_data_for_all_products_and_deployments(
  transaction_data_id integer,
  num_deployments integer DEFAULT 4)
  RETURNS integer AS
$BODY$
DECLARE
  product_id integer;
  pv_org_id integer;
BEGIN
  FOR product_id, pv_org_id IN SELECT p.product_id, p.pv_org_id FROM product p LOOP
    FOR i IN 1..num_deployments LOOP
      INSERT INTO deployment_product_to_transaction_data
        (product_id, pv_org_id, transaction_data_id, deployment_id)
      VALUES
        (product_id, pv_org_id, transaction_data_id, i);
    END LOOP;
  END LOOP;
  RETURN 0;
END;
$BODY$
LANGUAGE plpgsql;
