DELIMITER ;;
CREATE TRIGGER influencer_delete_trigger
    AFTER DELETE
    ON INFLUENCER_POST
    FOR EACH ROW
BEGIN
    SELECT lambda_async(
                   'arn:aws:lambda:us-west-1:474391618037:function:UpdateTypeSenseIndexFunction',
                   JSON_OBJECT(
						'action', 'DELETE',
                        'id', OLD.id,
                        'email', OLD.influencer_email
                   )
	)
    
    INTO @output;
END
;;
DELIMITER ;