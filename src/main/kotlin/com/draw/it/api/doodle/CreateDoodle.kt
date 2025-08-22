package com.draw.it.api.doodle

import com.draw.it.api.common.exception.BizException
import com.draw.it.api.common.exception.ErrorCode
import com.draw.it.api.doodle.domain.Doodle
import com.draw.it.api.doodle.domain.DoodleRepository
import com.draw.it.api.doodle.service.ImageStorageService
import com.draw.it.api.project.domain.ProjectRepository
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@Tag(name = "Doodle", description = "두들 API")
@RestController
@RequestMapping("/anonymous/project/{projectUuid}/doodle")
class CreateDoodle(
    private val doodleRepository: DoodleRepository,
    private val projectRepository: ProjectRepository,
    private val imageStorageService: ImageStorageService
) {

    @PostMapping(consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun createDoodle(
        @Parameter(description = "프로젝트 UUID", required = true)
        @PathVariable projectUuid: String,
        @Parameter(description = "작성자 닉네임", required = true)
        @RequestParam nickname: String,
        @Parameter(description = "편지 내용", required = false)
        @RequestParam(required = false) letter: String?,
        @Parameter(description = "두들 이미지 파일", required = true)
        @RequestParam image: MultipartFile
    ): CreateDoodleResponse {
        // 프로젝트 존재 여부 확인
        val project = projectRepository.findByUuid(projectUuid)
            ?: throw BizException(ErrorCode.BAD_REQUEST, "존재하지 않는 프로젝트입니다")

        // 이미지 업로드
        val imageUrl = imageStorageService.uploadImage(image)

        // Doodle 생성 및 저장
        val doodle = Doodle(
            projectId = project.id!!,
            projectUuid = projectUuid,
            nickname = nickname,
            letter = letter,
            imageUrl = imageUrl
        )

        val savedDoodle = doodleRepository.save(doodle)

        // 프로젝트의 모든 두들 조회
        val allDoodles = doodleRepository.findByProjectId(project.id!!)
        val otherDoodleImageUrls = allDoodles.filter { it.id != savedDoodle.id }.map { it.imageUrl }

        return CreateDoodleResponse.from(
            doodle = savedDoodle,
            projectName = project.topic,
            doodleCount = allDoodles.size,
            myDoodleImageUrl = savedDoodle.imageUrl,
            otherDoodleImageUrls = otherDoodleImageUrls
        )
    }
}

data class CreateDoodleResponse(
    val doodleId: Long,
    val projectTopic: String,
    val doodleCount: Int,
    val myDoodleImageUrl: String,
    val otherDoodleImageUrls: List<String>
) {
    companion object {
        fun from(
            doodle: Doodle,
            projectName: String,
            doodleCount: Int,
            myDoodleImageUrl: String,
            otherDoodleImageUrls: List<String>
        ): CreateDoodleResponse {
            return CreateDoodleResponse(
                doodleId = doodle.id!!,
                projectTopic = projectName,
                doodleCount = doodleCount,
                myDoodleImageUrl = myDoodleImageUrl,
                otherDoodleImageUrls = otherDoodleImageUrls
            )
        }
    }
}
