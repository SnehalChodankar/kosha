document.addEventListener('DOMContentLoaded', () => {
    const observerOptions = {
        root: null,
        rootMargin: '0px',
        threshold: 0.1
    };

    const observer = new IntersectionObserver((entries, observer) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                entry.target.style.opacity = '0';
                entry.target.style.transform = 'translateY(20px)';
                entry.target.style.transition = 'opacity 0.6s ease-out, transform 0.6s ease-out';
                
                // Trigger reflow
                void entry.target.offsetWidth;
                
                entry.target.style.opacity = '1';
                entry.target.style.transform = 'translateY(0)';
                
                observer.unobserve(entry.target);
            }
        });
    }, observerOptions);

    document.querySelectorAll('.animate-on-scroll').forEach(element => {
        // Initially hide elements
        element.style.opacity = '0';
        element.style.transform = 'translateY(20px)';
        observer.observe(element);
    });

    document.querySelectorAll('.feature-card').forEach(card => {
        const anim = card.querySelector('.animation-container');
        if (anim) {
            card.addEventListener('click', () => {
                // If we are opening this card, it will naturally expand. 
                // The min-height keeps it aligned with others when closed.
                anim.classList.toggle('active');
                if(anim.classList.contains('active')) {
                    anim.classList.remove('run-anim');
                    void anim.offsetWidth; // trigger reflow
                    anim.classList.add('run-anim');
                }
            });
        }
    });

    // Dynamic height alignment
    function alignCardHeights() {
        const cards = document.querySelectorAll('.feature-card');
        // Reset min-height to measure natural height
        cards.forEach(card => card.style.minHeight = '0px');
        let maxHeight = 0;
        cards.forEach(card => {
            // Measure only when animations are closed to get baseline height
            const anim = card.querySelector('.animation-container');
            const wasActive = anim && anim.classList.contains('active');
            if (wasActive) anim.classList.remove('active');
            
            if (card.offsetHeight > maxHeight) {
                maxHeight = card.offsetHeight;
            }
            
            if (wasActive) anim.classList.add('active');
        });
        // Apply max height
        cards.forEach(card => card.style.minHeight = maxHeight + 'px');
    }
    
    window.addEventListener('load', alignCardHeights);
    window.addEventListener('resize', alignCardHeights);
    alignCardHeights();
});
