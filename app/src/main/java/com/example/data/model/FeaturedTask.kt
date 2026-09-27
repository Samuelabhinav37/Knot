package com.example.data.model

import com.example.R

data class TaskStepItem(
    val stepNumber: Int,
    val title: String,
    val instruction: String,
    val durationText: String = ""
)

data class FeaturedTask(
    val id: String,
    val title: String,
    val category: String,
    val locationOrTag: String,
    val imageRes: Int,
    val durationMinutes: Int,
    val durationLabel: String,
    val difficulty: String, // e.g., "Beginner", "Moderate", "Challenging"
    val difficultyLevel: Int, // 1 to 5
    val metricLabel: String, // e.g., "320 kcal" or "110 kcal"
    val quote: String,
    val shortDescription: String,
    val fullOverview: String,
    val keyBenefits: List<String>,
    val steps: List<TaskStepItem>,
    val xpReward: Int = 150
)

object SampleTasksRepository {
    val tasks: List<FeaturedTask> = listOf(
        FeaturedTask(
            id = "task_cycling",
            title = "Coastal Cycling Sprint",
            category = "Cardio & Stamina",
            locationOrTag = "Outdoor Route",
            imageRes = R.drawable.img_cycling_task,
            durationMinutes = 35,
            durationLabel = "35 mins",
            difficulty = "Moderate",
            difficultyLevel = 3,
            metricLabel = "320 kcal",
            quote = "Steady cadence conquers every hill. Build your endurance stride.",
            shortDescription = "A sustained aerobic endurance ride to strengthen cardiovascular stamina, boost dopamine, and clear cognitive fatigue.",
            fullOverview = "This structured outdoor cycling session emphasizes sustained aerobic pacing combined with interval accelerations. Cycling strengthens lower-body endurance, joint resilience with low impact, and triggers neurochemical resets that enhance executive brain function for the remainder of your day.",
            keyBenefits = listOf(
                "Expands VO2 Max and cardiovascular stamina",
                "Gentle on knees with low-impact muscular loading",
                "Triggers natural endorphin release and clears brain fog"
            ),
            steps = listOf(
                TaskStepItem(
                    stepNumber = 1,
                    title = "Gear & Safety Check",
                    instruction = "Inspect tire pressure, fasten helmet securely, and prepare hydration bottle.",
                    durationText = "3 mins"
                ),
                TaskStepItem(
                    stepNumber = 2,
                    title = "Cadence Warmup",
                    instruction = "Spin in low gear resistance at 75–80 RPM to loosen knees and elevate core temperature.",
                    durationText = "5 mins"
                ),
                TaskStepItem(
                    stepNumber = 3,
                    title = "Steady Aerobic Cruise",
                    instruction = "Sustain a cadence of 85–95 RPM with deep rhythmic breathing along your open route.",
                    durationText = "20 mins"
                ),
                TaskStepItem(
                    stepNumber = 4,
                    title = "Power Surge Interval",
                    instruction = "Perform 2x 90-second high-output pushes with equal recovery spins between each.",
                    durationText = "4 mins"
                ),
                TaskStepItem(
                    stepNumber = 5,
                    title = "Decompression & Posture Reset",
                    instruction = "Drop down to easy resistance, sit tall, exhale deeply, and shake out hand grips.",
                    durationText = "3 mins"
                )
            ),
            xpReward = 150
        ),
        FeaturedTask(
            id = "task_walking",
            title = "Sunrise Power Walk",
            category = "Circadian Rhythm",
            locationOrTag = "Urban Trail",
            imageRes = R.drawable.img_walking_task,
            durationMinutes = 20,
            durationLabel = "20 mins",
            difficulty = "Beginner",
            difficultyLevel = 1,
            metricLabel = "115 kcal",
            quote = "Morning steps under natural light calibrate your daily focus.",
            shortDescription = "An invigorating brisk outdoor stride to synchronize circadian rhythms, stimulate lymphatic flow, and awaken mental clarity.",
            fullOverview = "Walking at a brisk pace in morning daylight acts as a primary circadian cue, lowering morning cortisol and initiating natural energy production. The rhythmic bilateral movement stimulates neurological balance and reduces postural tension accumulated from prolonged sitting.",
            keyBenefits = listOf(
                "Aligns natural circadian rhythms via morning daylight",
                "Decompresses spinal disks and relieves lower back stiffness",
                "Stimulates bilateral brain hemispheres for creative thinking"
            ),
            steps = listOf(
                TaskStepItem(
                    stepNumber = 1,
                    title = "Sunlight & Posture Setup",
                    instruction = "Step outside into natural light, drop shoulders down, and align head over spine.",
                    durationText = "2 mins"
                ),
                TaskStepItem(
                    stepNumber = 2,
                    title = "Natural Stride Warmup",
                    instruction = "Begin walking at an easy cadence, rolling from heel to toe smoothly.",
                    durationText = "3 mins"
                ),
                TaskStepItem(
                    stepNumber = 3,
                    title = "Brisk Power Stride",
                    instruction = "Accelerate to a purposeful power walk with a 90-degree arm pump and rhythmic breathing.",
                    durationText = "12 mins"
                ),
                TaskStepItem(
                    stepNumber = 4,
                    title = "Easy Grounding Cooldown",
                    instruction = "Slow your pace to a gentle stroll, take 4 slow nasal breaths, and drink fresh water.",
                    durationText = "3 mins"
                )
            ),
            xpReward = 100
        ),
        FeaturedTask(
            id = "task_stretching",
            title = "Mindful Mobility & Stretch",
            category = "Recovery & Zen",
            locationOrTag = "Studio Mat",
            imageRes = R.drawable.img_stretching_task,
            durationMinutes = 15,
            durationLabel = "15 mins",
            difficulty = "Gentle",
            difficultyLevel = 1,
            metricLabel = "65 kcal",
            quote = "A flexible body fosters a resilient and adaptable mind.",
            shortDescription = "A fluid full-body movement flow targeting hips, spine, and thoracic cage to dissolve tension and reset posture.",
            fullOverview = "Prolonged desk work contracts hip flexors and rounds upper backs. This mindful session combines joint traction, gentle myofascial lengthening, and parasympathetic breathing to release systemic tightness and down-regulate nervous system strain.",
            keyBenefits = listOf(
                "Restores hip and thoracic spine range of motion",
                "Releases accumulated neck and shoulder stress",
                "Down-regulates sympathetic fight-or-flight nervous states"
            ),
            steps = listOf(
                TaskStepItem(
                    stepNumber = 1,
                    title = "Diaphragmatic Breath Anchor",
                    instruction = "Sit comfortably on mat, close eyes, and take 5 slow breaths into your belly.",
                    durationText = "2 mins"
                ),
                TaskStepItem(
                    stepNumber = 2,
                    title = "Cat-Cow Spinal Waves",
                    instruction = "Move on hands and knees between gentle extension and flexion (10 repetitions).",
                    durationText = "3 mins"
                ),
                TaskStepItem(
                    stepNumber = 3,
                    title = "Runner's Lunge & Hip Flexor Opener",
                    instruction = "Step one foot forward into low lunge, tuck pelvis, reach arm high. Hold 60s each side.",
                    durationText = "4 mins"
                ),
                TaskStepItem(
                    stepNumber = 4,
                    title = "Thread-the-Needle Shoulder Release",
                    instruction = "From hands and knees, slide arm across chest to release upper back and neck.",
                    durationText = "3 mins"
                ),
                TaskStepItem(
                    stepNumber = 5,
                    title = "Supine Grounding Rest",
                    instruction = "Lie flat on back, relax jaw, and notice the restored ease through your entire body.",
                    durationText = "3 mins"
                )
            ),
            xpReward = 100
        ),
        FeaturedTask(
            id = "task_alpine_breath",
            title = "Alpine Breath & Mental Reset",
            category = "Mindfulness & Oxygen",
            locationOrTag = "Outdoor / Window",
            imageRes = R.drawable.img_andes_lake,
            durationMinutes = 10,
            durationLabel = "10 mins",
            difficulty = "Gentle",
            difficultyLevel = 1,
            metricLabel = "45 kcal",
            quote = "Deep resonant breath expands pulmonary volume and centers focus.",
            shortDescription = "Box breathing protocol paired with rhythmic oxygenation to clear cognitive fog, reduce heart rate variability stress, and anchor focus.",
            fullOverview = "This controlled respiration sequence leverages clinical box breathing (4s inhale, 4s hold, 4s exhale, 4s hold) and physiological sighs to instantly activate your parasympathetic nervous system and restore mental acuity.",
            keyBenefits = listOf(
                "Immediate down-regulation of stress and racing thoughts",
                "Optimizes oxygen-carbon dioxide balance for mental clarity",
                "Can be executed anywhere without special equipment"
            ),
            steps = listOf(
                TaskStepItem(
                    stepNumber = 1,
                    title = "Spinal Posture Anchor",
                    instruction = "Sit upright with feet grounded flat. Relax facial muscles and drop shoulders away from ears.",
                    durationText = "2 mins"
                ),
                TaskStepItem(
                    stepNumber = 2,
                    title = "4x4 Box Respiration Cycle",
                    instruction = "Inhale through nose for 4s, hold lungs full for 4s, exhale slowly for 4s, hold empty for 4s.",
                    durationText = "4 mins"
                ),
                TaskStepItem(
                    stepNumber = 3,
                    title = "Double-Inhale Physiological Sighs",
                    instruction = "Take two consecutive deep nasal inhales followed by one long, audible sigh through mouth (5 reps).",
                    durationText = "2 mins"
                ),
                TaskStepItem(
                    stepNumber = 4,
                    title = "Focus Anchor & Intention",
                    instruction = "Resume effortless natural breathing while silently visualizing your primary goal for today.",
                    durationText = "2 mins"
                )
            ),
            xpReward = 80
        ),
        FeaturedTask(
            id = "task_summit_cardio",
            title = "Summit Ascent Sprint",
            category = "High-Intensity Cardio",
            locationOrTag = "Mountain Trail",
            imageRes = R.drawable.img_mountain_inspiration,
            durationMinutes = 30,
            durationLabel = "30 mins",
            difficulty = "Challenging",
            difficultyLevel = 4,
            metricLabel = "380 kcal",
            quote = "Every steep incline builds uncompromising resilience and grit.",
            shortDescription = "High-tempo incline intervals designed to test lactate threshold, build athletic leg drive, and ignite metabolism.",
            fullOverview = "Designed for peak cardiovascular conditioning, this session alternates between tempo climbing efforts and active recovery. Elevated grade running or stair strides target fast-twitch muscle recruitment while demanding intense focus.",
            keyBenefits = listOf(
                "Elevates lactate threshold and maximum aerobic power",
                "Rapidly engages glutes, calves, and posterior chain",
                "High post-exercise metabolic burn (EPOC effect)"
            ),
            steps = listOf(
                TaskStepItem(
                    stepNumber = 1,
                    title = "Dynamic Joint Mobilization",
                    instruction = "Perform 20 high knees, 20 butt kicks, and ankle rotations to prep Achilles and knee joints.",
                    durationText = "4 mins"
                ),
                TaskStepItem(
                    stepNumber = 2,
                    title = "Gradual Incline Pacing",
                    instruction = "Begin steady uphill ascent at 65% perceived effort to elevate core body temperature.",
                    durationText = "6 mins"
                ),
                TaskStepItem(
                    stepNumber = 3,
                    title = "High-Intensity Incline Surges",
                    instruction = "Execute 4x 60-second near-maximal uphill sprints, recovering with slow downhill walking between each.",
                    durationText = "12 mins"
                ),
                TaskStepItem(
                    stepNumber = 4,
                    title = "Aerobic Flush Stride",
                    instruction = "Transition to flat ground walking to flush metabolites from working leg muscles.",
                    durationText = "5 mins"
                ),
                TaskStepItem(
                    stepNumber = 5,
                    title = "Calf & Hamstring Lengthening",
                    instruction = "Hold static calf and hamstring stretches for 45 seconds each while drinking electrolyte fluid.",
                    durationText = "3 mins"
                )
            ),
            xpReward = 220
        )
    )
}
