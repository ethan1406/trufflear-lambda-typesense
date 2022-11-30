package lambda.frameworks.mappers

import com.amazonaws.services.lambda.runtime.LambdaLogger
import com.trufflear.lambda.configs.TriggerConfigs
import com.trufflear.lambda.mappers.toIndexAction
import com.trufflear.lambda.services.StorageService
import com.trufflear.lambda.triggers.models.IndexAction
import com.trufflear.lambda.triggers.models.TriggerAction
import com.trufflear.lambda.util.dateFormat
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

private const val email = "email"
private const val id = "id"

class TriggerActionToIndexActionTest {

    private val logger = mock<LambdaLogger>()

    private val storageService = mock<StorageService>()

    @Test
    fun `to index action should map to null when email is missing`() {
        // ARRANGE
        val map = mapOf(TriggerConfigs.postId to "id")

        // ACT
        val indexAction = toIndexAction(map, storageService, logger)

        // ASSERT
        assertThat(indexAction).isNull()
    }

    @Test
    fun `to index action should map to null when action is unknown`() {
        // ARRANGE
        val map = mapOf(
            TriggerConfigs.postId to "id",
            TriggerConfigs.email to "email",
            TriggerConfigs.action to "RandomAction"
        )

        // ACT
        val indexAction = toIndexAction(map, storageService, logger)

        // ASSERT
        assertThat(indexAction).isNull()
    }

    @Test
    fun `to index action should map to delete action`() {
        // ARRANGE
        val map = mapOf(
            TriggerConfigs.postId to id,
            TriggerConfigs.email to email,
            TriggerConfigs.action to TriggerAction.DELETE.name
        )

        // ACT
        val indexAction = toIndexAction(map, storageService, logger)

        // ASSERT
        assertThat(indexAction is IndexAction.Delete).isTrue
        assertThat((indexAction as IndexAction.Delete).email).isEqualTo(email)
        assertThat(indexAction.postId).isEqualTo(id)
    }

    @Test
    fun `to index action should map to upsert action for update trigger`() {
        // ARRANGE
        val timestamp = "2022-11-13 14:20:51.000000"
        val objectKey = "object_key"
        val url = "url"
        whenever(storageService.getUrl(objectKey)).thenReturn(url)

        val map = mapOf(
            TriggerConfigs.postId to id,
            TriggerConfigs.email to email,
            TriggerConfigs.action to TriggerAction.UPDATE.name,
            TriggerConfigs.caption to "caption",
            TriggerConfigs.mentions to "mentions",
            TriggerConfigs.hashtags to "hashtags",
            TriggerConfigs.permalink to "permalink://",
            TriggerConfigs.thumbnailObjectKey to objectKey,
            TriggerConfigs.createdAtTimeStamp to timestamp
        )

        // ACT
        val indexAction = toIndexAction(map, storageService, logger)

        // ASSERT
        assertThat(indexAction is IndexAction.Upsert).isTrue
        assertThat((indexAction as IndexAction.Upsert).email).isEqualTo(email)
        assertThat(indexAction.thumbnailUrl).isEqualTo(url)
        assertThat(indexAction.createdAtTimeMillis).isEqualTo(dateFormat.parse(timestamp).time)
    }

    @Test
    fun `to index action should map to upsert action for insert trigger`() {
        // ARRANGE
        val timestamp = "malformed"
        val objectKey = "object_key"
        val url = "url"
        whenever(storageService.getUrl(objectKey)).thenReturn(url)

        val map = mapOf(
            TriggerConfigs.postId to id,
            TriggerConfigs.email to email,
            TriggerConfigs.action to TriggerAction.UPDATE.name,
            TriggerConfigs.caption to "caption",
            TriggerConfigs.mentions to "mentions",
            TriggerConfigs.hashtags to "hashtags",
            TriggerConfigs.permalink to "permalink://",
            TriggerConfigs.thumbnailObjectKey to objectKey,
            TriggerConfigs.createdAtTimeStamp to timestamp
        )

        // ACT
        val indexAction = toIndexAction(map, storageService, logger)

        // ASSERT
        assertThat(indexAction is IndexAction.Upsert).isTrue
        assertThat((indexAction as IndexAction.Upsert).email).isEqualTo(email)
        assertThat(indexAction.thumbnailUrl).isEqualTo(url)
        assertThat(indexAction.createdAtTimeMillis).isEqualTo(0L)
    }
}