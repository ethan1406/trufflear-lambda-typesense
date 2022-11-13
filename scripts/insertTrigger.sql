DELIMITER ;;
CREATE TRIGGER influencer_test_trigger
    AFTER INSERT
    ON INFLUENCER_POST
    FOR EACH ROW
BEGIN
    SELECT lambda_async(
                   'arn:aws:lambda:us-west-1:474391618037:function:UpdateTypeSenseIndexFunction',
                   CONCAT('{ "action": "INSERT",','"caption":"', NEW.caption,'",','"id":"', NEW.id,'",','"email":"', NEW.influencer_email,'",','"created_at_timestamp":"', NEW.created_at_timestamp,'",','"permalink":"', NEW.permalink,'",','"hashtags":"',NEW.hashtags,'",','"mentions":"',New.mentions,'","thumbnail_url":"', New.thumbnail_url,'"}'))
    INTO @output;
END
;;
DELIMITER ;