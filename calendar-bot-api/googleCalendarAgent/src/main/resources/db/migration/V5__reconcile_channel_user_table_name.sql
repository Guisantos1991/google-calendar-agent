DO $$
BEGIN
    IF to_regclass('public.channel_user') IS NOT NULL
       AND to_regclass('public.channel_users') IS NOT NULL THEN
        RAISE EXCEPTION 'Both channel_user and channel_users tables exist. Manual reconciliation is required before migration can continue.';
    END IF;

    IF to_regclass('public.channel_user') IS NULL
       AND to_regclass('public.channel_users') IS NOT NULL THEN
        ALTER TABLE public.channel_users RENAME TO channel_user;
    END IF;

    IF to_regclass('public.ux_channel_users_channel_external_user') IS NOT NULL
       AND to_regclass('public.ux_channel_user_channel_external_user') IS NULL THEN
        ALTER INDEX public.ux_channel_users_channel_external_user
            RENAME TO ux_channel_user_channel_external_user;
    END IF;
END
$$;
