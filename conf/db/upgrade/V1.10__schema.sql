# VmInstanceEO
DROP TRIGGER IF EXISTS trigger_clean_ImageEO_for_VmInstanceEO;
DELIMITER $$
CREATE TRIGGER trigger_clean_ImageEO_for_VmInstanceEO AFTER DELETE ON `VmInstanceEO`
FOR EACH ROW
    BEGIN
        DELETE FROM ImageEO WHERE `deleted` IS NOT NULL AND `uuid` NOT IN (SELECT imageUuid FROM VmInstanceVO WHERE imageUuid IS NOT NULL);
    END $$
DELIMITER ;

# VolumeEO
DROP TRIGGER IF EXISTS trigger_clean_AccountResourceRefVO_for_VolumeEO;
DELIMITER $$
CREATE TRIGGER trigger_cleanup_for_VolumeEO_soft_delete AFTER UPDATE ON `VolumeEO`
FOR EACH ROW
    BEGIN
        IF OLD.`deleted` IS NULL AND NEW.`deleted` IS NOT NULL THEN
            DELETE FROM `AccountResourceRefVO` WHERE `resourceUuid` = OLD.`uuid` AND `resourceType` = 'VolumeVO';
            DELETE FROM `SystemTagVO` WHERE `resourceUuid` = OLD.`uuid` AND `resourceType` = 'VolumeVO';
            DELETE FROM `UserTagVO` WHERE `resourceUuid` = OLD.`uuid` AND `resourceType` = 'VolumeVO';
        END IF;
    END$$
DELIMITER ;

DROP TRIGGER IF EXISTS trigger_cleanup_for_VolumeEO_hard_delete;
DELIMITER $$
CREATE TRIGGER trigger_cleanup_for_VolumeEO_hard_delete AFTER DELETE ON `VolumeEO`
FOR EACH ROW
    BEGIN
            DELETE FROM `AccountResourceRefVO` WHERE `resourceUuid` = OLD.`uuid` AND `resourceType` = 'VolumeVO';
            DELETE FROM `SystemTagVO` WHERE `resourceUuid` = OLD.`uuid` AND `resourceType` = 'VolumeVO';
            DELETE FROM `UserTagVO` WHERE `resourceUuid` = OLD.`uuid` AND `resourceType` = 'VolumeVO';
    END $$
DELIMITER ;

