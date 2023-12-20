package org.edec.commons.entity.dec.aim;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.json.JSONObject;

import javax.persistence.*;

/**
 * @author Max Dimukhametov
 */
@Getter
@Setter
@NoArgsConstructor
/*@Entity
@Table(name = "aim_task")*/ public class AimTask {
    @Id
    @Column(name = "id_aim_task")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Integer point;

    @Column
    private String condition;

    @ManyToOne
    @JoinColumn(name = "id_aim")
    private Aim aim;
    @ManyToOne
    @JoinColumn(name = "id_aim_level_involvement")
    public AimLevelInvolvement levelInvolvement;
    @ManyToOne
    @JoinColumn(name = "id_aim_task_type")
    private AimTaskType typeTask;

    public JSONObject getJsonCondition () {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(condition);
        } catch (Exception ignored) {
        }
        return jsonObject;
    }
}
