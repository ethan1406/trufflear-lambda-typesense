DELIMITER ;;
CREATE TRIGGER influencer_update_trigger
    AFTER UPDATE
    ON INFLUENCER_POST
    FOR EACH ROW
BEGIN
    SELECT lambda_async(
                   'arn:aws:lambda:us-west-1:474391618037:function:UpdateTypeSenseIndexFunction',
                   CONCAT('{ "action": "UPDATE",','"caption":"', NEW.caption,'",','"id":"', NEW.id,'",','"email":"', NEW.influencer_email,'",','"permalink":"', NEW.permalink,'",','"hashtags":"',NEW.hashtags,'",','"mentions":"', New.mentions,'"}'))
    INTO @output;
END
;;
DELIMITER ;