DELIMITER ;;
CREATE TRIGGER influencer_insert_trigger
    AFTER INSERT
    ON INFLUENCER_POST
    FOR EACH ROW
BEGIN
    SELECT lambda_async(
                   'arn:aws:lambda:us-west-1:474391618037:function:UpdateTypeSenseIndexFunction',
                   JSON_OBJECT(
						'action', 'INSERT',
                        'caption', NEW.caption,
                        'email', NEW.influencer_email,
                        'id', NEW.id,
                        'created_at_timestamp', NEW.created_at_timestamp,
                        'permalink', NEW.permalink,
                        'hashtags', NEW.hashtags,
                        'mentions', NEW.mentions,
                        'thumbnail_object_key', NEW.thumbnail_object_key
                   )
		   )
    INTO @output;
END
;;
DELIMITER ;