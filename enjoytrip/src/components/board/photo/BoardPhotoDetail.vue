<script setup>
import { ref, onMounted } from 'vue';
import { getPhoto, getComments, modifyPhoto, writeComment, deletePhoto } from "@/api/boardPhoto.js";
import BoardPhotoCommentItem from "./item/BoardPhotoCommentItem.vue";
import { useMemberStore } from "@/stores/user";
import { storeToRefs } from "pinia";
import { useRoute, useRouter } from "vue-router";

const router = useRouter();

const props = defineProps({
    photoId: Number,
});

const emit = defineEmits(['cancelDetail']);

const memberStore = useMemberStore();
const { isLogin, userInfo } = storeToRefs(memberStore);

const photo = ref({}); // 게시글 dto
const comments = ref([]);
const comment = ref(""); // 새로 남긴 댓글
const currIdx = ref(0); // 사진 인덱스

const photoLoaded = ref(false); // photo 데이터 로드 여부를 나타내는 변수
const commentsLoaded = ref(false); // comments 데이터 로드 여부를 나타내는 변수

const toggleContentState = ref(true); // 내용 readOnly 설정용
const contentInput = ref("");


onMounted(() => {
    // console.log("디테일 뷰 들어옴!");

    // 게시글 조회 
    detailPhoto();

    // 댓글 조회 
    getComment();

});

const detailPhoto = () => {
    getPhoto(
        props.photoId,
        ({ data }) => {
            // console.log(JSON.stringify(data));
            // console.log("photo:" + data);
            // console.log(data.photoPaths);
            photo.value = data;
            photoLoaded.value = true; // photo 데이터 로드 완료
        },
        (error) => {
            console.log(error);
        }
    )
}

const getComment = () => {
    getComments(
        props.photoId,
        ({ data }) => {
            // console.log("comments:" + data);
            comments.value = data;
            commentsLoaded.value = true; // comments 데이터 로드 완료
        },
        (error) => {
            console.log(error);
        }
    );
};

const modifyContent = () => {

    if (!toggleContentState.value) { // 수정하러 가기 
        console.log(photo.value.content); // 입력한 내용

        // 수정하러 가! (boardPhotoId, content만 들고가면 됨)
        modifyPhoto(
            photo.value.boardPhotoId,
            photo.value,
            () => {
                console.log("게시글 수정 성공");
            },
            (error) => {
                console.log(error);
            }
        )

        toggleContentState.value = !toggleContentState.value;
    }
    else { // 수정 상태로 변경 
        toggleContentState.value = !toggleContentState.value;
        contentInput.value.focus();
    }
}

const writeComments = () => {
    // console.log("입력한 댓글:", comment.value);
    // console.log("현재 유저:", userInfo.value.userId);

    let commentJson = {
        content: comment.value,
        userId: userInfo.value.userId
    };

    writeComment(
        photo.value.boardPhotoId,
        commentJson,
        () => {
            console.log("댓글 작성 성공");
            comment.value = '';
            getComment();
        },
        (error) => {
            console.log(error);
        }
    )
}

const deletePhotos = () => {

    if (confirm("정말로 삭제하시겠습니까?")) {
        deletePhoto(
            photo.value.boardPhotoId,
            () => {
                console.log("삭제 성공");
                // router.push({ name: "board-photo-list" });
                emit('cancelDetail');
            }
        )
    }
}


</script>

<template>
    <transition name="modal" appear>
        <div v-if="photoLoaded && commentsLoaded" class="modal modal-overlay" @click.self="$emit('close')">
            <div class="modal-window">
                <div class="modal-content">
                    <div class="box1">
                        <div v-if="photoLoaded && commentsLoaded" class='preview-image'>
                            <template v-if="photo.photoPaths.length == 1">
                                <img :src="'http://localhost:80/upload/' + photo.photoPaths[0]" style="height:100%;">
                            </template>
                            <template v-else>
                                <span id="currPage">{{ currIdx + 1 }}/{{ photo.photoPaths.length }} </span>
                                <i v-if='currIdx > 0' id='idxMinus' class="fa-solid fa-circle-chevron-left fa-xl"
                                    @click='currIdx--'></i>
                                <img :src="'http://localhost:80/upload/' + photo.photoPaths[currIdx]" style="height:100%;">
                                <i v-if='currIdx < photo.photoPaths.length - 1' id='idxPlus'
                                    class="fa-solid fa-circle-chevron-right fa-xl" @click='currIdx++'></i>
                            </template>
                        </div>
                    </div>

                    <div class="rightBox">
                        <div class="box2">
                            <div class="box2-1">
                                <span id="userId" style="font-weight: bold;">{{ photo.userId }}</span>
                                <div class="box2-1-1">
                                    <!-- <i class="fa-solid fa-ellipsis"></i> -->
                                    <button @click.prevent='modifyContent'>수정</button>
                                    <button @click.prevent='deletePhotos'>삭제</button>
                                    <i class="fa-solid fa-xmark" @click.prevent="$emit('cancelDetail')"></i>
                                </div>
                            </div>
                            <div class="box2-2">
                                <span style="font-size:x-small">{{ photo.detailPlace }}</span>
                                <span style="font-size:x-small">{{ photo.registerTime }}</span>
                            </div>
                        </div>
                        <div class="box3">
                            <textarea :readOnly="toggleContentState" v-model="photo.content" ref="contentInput"></textarea>
                        </div>
                        <div class="box4">
                            <BoardPhotoCommentItem v-for="cmt in comments" :comment="cmt" :key="cmt.commentId" />
                        </div>
                        <div class="box5">
                            <i class="fa-solid fa-heart" style="color: #db0000;"></i>
                            <!-- <i class="fa-regular fa-heart" style="color: #db0000;"></i> -->
                            <input type="text" placeholder="댓글을 남겨보세요 !" v-model='comment'>
                            <button @click.prevent='writeComments'>등록</button>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </transition>
</template>

<style scoped>
textarea {
    border-width: 0;
    resize: none;
    width: 98%;
    max-width: 98%;
    overflow: auto;
    height: 100%;
    overflow-wrap: break-word;
}

textarea:focus {
    outline: none;
}

#idxMinus {
    position: absolute;
    top: 50%;
    left: 10px;
    cursor: pointer;
}

#idxPlus {
    position: absolute;
    top: 50%;
    right: 10px;
    cursor: pointer;
}

#currPage {
    position: absolute;
    right: 10px;
    top: 1%;
    background-color: rgba(0, 0, 0, 0.4);
    padding: 3px 7px;
    border-radius: 10px;
}

i {
    color: rgba(0, 0, 0, 0.7);
}


.preview-image {
    height: 100%;
    width: 100%;
    /* background-color: aqua; */
    position: relative;
}

.cmt {
    border: 1px solid black;
}

.preview-image img {
    height: 100%;
    width: 100%;
}

input {
    outline: none;
    background: none;
    border-width: 0 0 1px;
    /* size:50px; */
    width: 70%;
}

input:focus {
    border-width: 0 0 2px;
}

.noBorderBtn {
    border: none;
    background-color: transparent;
}

button {
    padding: 5px 10px;
    /* margin: 5px; */
    /* margin-bottom: 10px; */
    border-radius: 13px;
    background-color: white;
    font-weight: 700;
    font-size: 0.9rem;
}

button:hover {
    cursor: pointer;
    border-color: #d20000;
}

.box2-1-1 button {
    padding: 4px 4px;
    /* margin: 5px; */
    /* margin-bottom: 10px; */
    border-radius: 10px;
    background-color: white;
    font-weight: 700;
    font-size: 0.6rem;
    margin-left: 5px;
}

.modal.modal-overlay {
    display: flex;
    align-items: center;
    justify-content: center;
    position: fixed;
    z-index: 30;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background: rgba(0, 0, 0, 0.5);
}

.modal-window {
    background: #fff;
    border-radius: 4px;
    overflow: hidden;
    width: 80%;
    height: 80%;
    display: inline-block;
}

.modal-content {
    /* margin: 2% 1%; */
    display: flex;
    height: 100%;
}

.rightBox {
    display: flex;
    flex-direction: column;
    height: 100%;
    width: 40%;
}


.modal-enter-active,
.modal-leave-active {
    transition: opacity 0.4s;
}


.modal-enter-active .modal-window,
.modal-leave-active .modal-window {
    transition: opacity 0.4s, transform 0.4s;
}

.modal-leave-active {
    transition: opacity 0.6s ease 0.4s;
}

.modal-enter,
.modal-leave-to {
    opacity: 0;
}

.modal-enter .modal-window,
.modal-leave-to .modal-window {
    opacity: 0;
    transform: translateY(-20px);
}

.box1 {
    height: 100%;
    width: 60%;
    display: flex;
    justify-content: center;
    align-items: center;
    border-right: solid 1px lightgray;
    position: relative;
    /* 부모 요소에 relative position을 추가합니다. */

}

.box2 {
    height: 10%;
    width: 100%;
    display: flex;
    flex-direction: column;
    border-bottom: solid 1px lightgray;
}

.box2-1 {
    height: 60%;
    /* background-color: rgb(182, 182, 16); */
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin: 0px 5px;
}

.box2-1 i {
    margin-left: 10px;
    cursor: pointer;
}

.box2-2 {
    height: 40%;
    /* background-color: lightseagreen; */
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin: 0px 5px;
}


.box3 {
    height: 25%;
    width: 100%;
    margin: 5px 5px;
    overflow-y: scroll;
    /* background-color: lightsteelblue; */
}

.box4 {
    height: 55%;
    width: 100%;
    border-top: solid 1px lightgray;
    /* background-color: blanchedalmond; */
    overflow-y: scroll;
}

/* scroll bar custom */
/* html {
    scrollbar-width: thin;
    scrollbar-color: lightgray transparent;
} */

/* Chrome, Safari 등 Webkit 기반 브라우저에서 스크롤바 스타일 지정 */
::-webkit-scrollbar {
    width: 6px;
}

::-webkit-scrollbar-track {
    background-color: transparent;
}

::-webkit-scrollbar-thumb {
    border-radius: 3px;
    background-color: lightgray;
}

::-webkit-scrollbar-button {
    width: 0;
    height: 0;
}


.box5 {
    height: 10%;
    width: 100%;
    display: flex;
    justify-content: space-evenly;
    align-items: center;
    border-top: solid 1px lightgray;
}
</style>