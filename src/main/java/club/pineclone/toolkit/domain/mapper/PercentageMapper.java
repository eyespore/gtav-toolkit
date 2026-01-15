package club.pineclone.toolkit.domain.mapper;

import club.pineclone.toolkit.domain.vo.macro.PercentageVO;
import club.pineclone.toolkit.domain.dto.macro.PercentageDTO;
import org.mapstruct.Mapper;

@Mapper
public interface PercentageMapper {

    PercentageVO toVO(PercentageDTO dto);

    PercentageDTO toDTO(PercentageVO vo);

}
