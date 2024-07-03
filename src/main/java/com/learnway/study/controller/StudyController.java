package com.learnway.study.controller;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.learnway.study.domain.ChatRoom;
import com.learnway.study.domain.Study;
import com.learnway.study.domain.StudyProblemImg;
import com.learnway.study.domain.StudyRepository;
import com.learnway.study.domain.StudyTag;
import com.learnway.study.dto.ChatRoomDto;
import com.learnway.study.dto.StudyDto;
import com.learnway.study.dto.StudyProblemDto;
import com.learnway.study.dto.StudyProblemImgDto;
import com.learnway.study.dto.StudyTagDto;
import com.learnway.study.service.StudyChatService;
import com.learnway.study.service.StudyPostService;
import com.learnway.study.service.StudyProblemImgService;
import com.learnway.study.service.StudyProblemService;
import com.learnway.study.service.StudyService;
import com.learnway.study.service.StudyTagService;

@Controller
@RequestMapping
public class StudyController {
	
	
	@Autowired
	private StudyService studyService; 
	@Autowired
	StudyRepository studyRepository;
	@Autowired
	StudyPostService studyPostService;
	@Autowired
	StudyTagService studyTagService;
	@Autowired
	StudyProblemService studyProblemService; 
	@Autowired
	StudyProblemImgService studyProblemImgService; 
	@Autowired
	StudyChatService studyChatService;
	
	
	

//	@RequestMapping(value="/studylist",method= {RequestMethod.GET,RequestMethod.POST})
	@GetMapping(value="/studylist")
	public String study(@PageableDefault(size=2) Pageable pageable,Model model) {
		Page<Study> studies = studyPostService.getBoardList(pageable);
		
		 int startPage = Math.max(1, studies.getPageable().getPageNumber() - 4);
	     int endPage = Math.min(studies.getPageable().getPageNumber()+4, studies.getTotalPages());
        
        
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("list", studies);
		
		return "/study/studylist";
	}
	
	@GetMapping(value="/studyadd")
	public String studyAddView() {
		return "/study/studyadd";
	}
	
	
	//수정 view메서드
	@PostMapping(value="/studyupdateview")
	public String studyUpdateView(StudyDto dto,Model model) {
	
		System.out.println(dto.getPostid()+ " 게시글id");
		Optional<Study> study =  studyService.updateView(dto);
		if(study.isPresent()) {
			model.addAttribute("study",study.get());
			model.addAttribute("postid",dto.getPostid());
			return "/study/studyupdate";
		}
		else {
			model.addAttribute("errmsg","게시글을 찾을 수 없습니다.");
			return "error/404";
		}
	}
	
	
	//게시글 추가 메서드
	@PostMapping(value="/studyadd")
	public String studyadd(StudyDto studyDto,ChatRoomDto chatRoomDto,StudyTagDto studyTagDto,
			StudyProblemDto studyProblemDto,StudyProblemImgDto studyProblemImgDto,
			@RequestParam("imgpath") MultipartFile[] files,Principal principal) {
		

		studyService.crateBoard(studyDto,chatRoomDto,studyTagDto,studyProblemDto,studyProblemImgDto,files,principal);
		return "redirect:/studylist";
	}
	
	//게시글 수정 메서드
	@PostMapping(value="/studyupdate")
	public String studyUpdate(StudyDto studyDto,ChatRoomDto chatRoomDto,StudyTagDto studyTagDto,
			StudyProblemDto studyProblemDto,StudyProblemImgDto studyProblemImgDto,
			@RequestParam("imgpath") MultipartFile[] files,Principal principal) {
		
		System.out.println("게시글 id 수정창" + studyDto.getPostid());
		studyService.updateBoard(studyDto,chatRoomDto,studyTagDto,studyProblemDto,studyProblemImgDto,files,principal);
		
		return "redirect:/studylist";
	}
	
	
	@GetMapping(value="/study/detail/"+"{postid}")
	public String studydetail(@PathVariable("postid") Integer postId,Model model,Principal principal) {
		Optional<Study> optionalStudy = studyRepository.findById(postId);
		List<StudyTag> tagList = studyTagService.findTag(postId);
		
		//포스트id로 문제id 조회
		int problemId = studyProblemService.problemId(postId);
		//postid로 ChatRoomId 조회
		List<ChatRoom> chatRoom = studyChatService.chatRoomId(postId);
		//problemId로 problemImgPathId조회
		List<StudyProblemImg> imgList = studyProblemImgService.problemImgPath(problemId);
		
		boolean result = studyService.boardCheck(problemId, principal);
		
		
		if(optionalStudy.isPresent()) {
			Study study = optionalStudy.get();
			model.addAttribute("study",study);
			model.addAttribute("studyTag",tagList);
			model.addAttribute("imgList",imgList);
			model.addAttribute("chatRoom",chatRoom);
			model.addAttribute("hostList",result);
			return "study/studydetail";
		}else {
			model.addAttribute("errmsg","게시글을 찾을 수 없습니다.");
			return "error/404";
		}
		
		}
		

	
	
	

}
