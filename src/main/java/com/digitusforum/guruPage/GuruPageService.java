package com.digitusforum.guruPage;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class GuruPageService {

	@Autowired
	GuruPageRepository guruPageRepository;

	public List<GuruPageEntity> retrieveByGuruId(GuruPageVO guruPageVO) {
		if (guruPageVO == null || StringUtils.isBlank(guruPageVO.getGuruId()))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, M.GURU_PAGE_MISSING_GURU_ID);
		return guruPageRepository.findByGuruIdAndDeletedIsFalseOrderByPositionAsc(guruPageVO.getGuruId());
	}

}
