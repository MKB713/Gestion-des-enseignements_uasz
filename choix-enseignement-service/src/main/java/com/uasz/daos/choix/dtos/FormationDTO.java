
package com.uasz.daos.choix.dtos;
import com.uasz.daos.choix.dtos.FiliereDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FormationDTO {
    private Long id;
    private String code;
    private String libelle;
    private String description;
    private Date dateCreation;
    private FiliereDTO filiere; 
    private com.uasz.daos.choix.dto.NiveauDTO niveau;  
    private String statutFormation;
}