function diffNumPktsToSendSignal_vs_AccuracyFN()
    data=load('C:\workspace\projects\eclipse\MultiSwitchCM\data\diffNumPktsToSendSignal_vs_AccuracyFN\diffNumPktsToSendSignal_vs_AccuracyFN.txt');

    %size
    sizeData=size(data);
    rows=sizeData(1);
    %one tenth memory
    hundredthMem=data(1,2);
    
    accuracy(1,1,1)=1;
    accuracySD(1,1,1)=1;
    fn(1,1,1)=1;
    fnSD(1,1,1)=1;
    for i=1:rows
        ithMem=ithMemory(hundredthMem, data(i,2));
        accuracy(ithMem, log10(data(i,7))-2)=data(i,3);
        accuracySD(ithMem, log10(data(i,7))-2)=data(i,4);
        fn(ithMem, log10(data(i,7))-2)=data(i,5);
        fnSD(ithMem, log10(data(i,7))-2)=data(i,6);
    end
    
    %accuracy
    figure
    hold on;    
    %errorbar(1:4, accuracy(1,:), accuracySD(1,:), '-rv', 'linewidth', 1.5);
    %errorbar(1:4, accuracy(2,:), accuracySD(2,:), '-g>', 'linewidth', 1.5);
    errorbar(1:4, accuracy(3,:), accuracySD(3,:), '-r^', 'linewidth', 1.5);
    errorbar(1:4, accuracy(4,:), accuracySD(4,:), '-g+', 'linewidth', 1.5);
    errorbar(1:4, accuracy(5,:), accuracySD(5,:), '-bo', 'linewidth', 1.5);
    errorbar(1:4, accuracy(6,:), accuracySD(6,:), '-m<', 'linewidth', 1.5);
    errorbar(1:4, accuracy(7,:), accuracySD(7,:), '-c>', 'linewidth', 1.5);

    set(gca, 'FontSize', 18,'XTick', 1:4, 'xticklabel', {1000, 10000, 100000, 1000000});
    legend('#buckets=20604', '#buckets=51510', '#buckets=103020', '#buckets=154530',  '#buckets=206040');
    legend('Location', 'southwest');
    legend('boxoff');
    xlabel('Send Condition when receiving #pkts')
    ylabel('accuracy')
    xlim([0.95,4.05])
    ylim([0.75,0.96])
    hold off;
    
    %false negative
    figure
    hold on;    
    %errorbar(1:4, fn(1,:), fnSD(1,:), '-rv', 'linewidth', 1.5);
    %errorbar(1:4, fn(2,:), fnSD(2,:), '-g>', 'linewidth', 1.5);
    errorbar(1:4, fn(3,:), fnSD(3,:), '-r^', 'linewidth', 1.5);
    errorbar(1:4, fn(4,:), fnSD(4,:), '-g+', 'linewidth', 1.5);
    errorbar(1:4, fn(5,:), fnSD(5,:), '-bo', 'linewidth', 1.5);
    errorbar(1:4, fn(6,:), fnSD(6,:), '-m<', 'linewidth', 1.5);
    errorbar(1:4, fn(7,:), fnSD(7,:), '-c>', 'linewidth', 1.5);

    set(gca, 'FontSize', 18,'XTick', 1:4, 'xticklabel', {1000, 10000, 100000, 1000000});
    %{2060, 10302, 20604, 51510, 103020, 154530, 206040}
    legend('#buckets=20604', '#buckets=51510', '#buckets=103020', '#buckets=154530',  '#buckets=206040');
    legend('Location', 'northwest');
    legend('boxoff');
    xlabel('Send Condition when receiving #pkts')
    ylabel('false negative')
    xlim([0.95,4.05])
    ylim([0.02,0.15])
    hold off;
end

function idx=ithMemory(hundredthMem, mem)
    if hundredthMem == mem
        idx=1;
        return;
    end
    times=int16(mem/hundredthMem);
    
    %idx=int16(times/2.5)+1;
    %{0.01, 0.05, 0.1, 0.25, 0.5, 0.75, 1 }
    if times==1
        idx=1;
    end
    if times==5
        idx=2;
    end
    if times==10
        idx=3;
    end
    if times==25
        idx=4;
    end
    if times==50
        idx=5;
    end
    if times==75
        idx=6;
    end
    if times==100
        idx=7;
    end
end

